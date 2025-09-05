"use client";

import React, { useEffect, useRef, useState } from "react";
import {
    Room,
    LocalParticipant,
    RemoteTrack,
    RemoteTrackPublication,
    DataPacket_Kind,
    joinLiveKitRoom,
} from "@/lib/livekit";
import toast from "react-hot-toast";
import { RoomEvent } from "livekit-client";

export default function SellerPage() {
    const videoRef = useRef<HTMLVideoElement>(null);
    const [room, setRoom] = useState<Room | null>(null);
    const [chat, setChat] = useState<string[]>([]);
    const [msg, setMsg] = useState("");
    const [isConnected, setIsConnected] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        let mounted = true;

        const connect = async () => {
            try {
                setIsLoading(true);
                const token = await fetch(
                    `${process.env.NEXT_PUBLIC_API_URL}/api/livekit/token?room=shop1&identity=seller&isPublisher=true`
                ).then((r) => {
                    if (!r.ok) throw new Error("Failed to fetch token");
                    return r.text();
                });

                const r = await joinLiveKitRoom(
                    process.env.NEXT_PUBLIC_LIVEKIT_WS_URL ||
                        "ws://localhost:7881",
                    token
                );
                if (!mounted) {
                    r.disconnect?.();
                    return;
                }
                setRoom(r);
                setIsConnected(true);

                // Publish camera + mic
                const tracks = await r.localParticipant.createTracks({
                    audio: true,
                    video: true,
                });
                for (const t of tracks) {
                    try {
                        // some track objects expose attach directly
                        if (t.kind === "video" && videoRef.current) {
                            t.attach(videoRef.current);
                        }
                    } catch (e) {
                        console.warn("Seller: attaching local track failed", e);
                    }

                    // publish
                    await r.localParticipant.publishTrack(t);
                }

                // Listen for data messages
                r.on(
                    RoomEvent.DataReceived,
                    (
                        payload: Uint8Array,
                        participant?: { identity: string } | undefined
                    ) => {
                        try {
                            const s = new TextDecoder().decode(payload);
                            console.log("Received message:", s);

                            setChat((prev) => [
                                ...prev,
                                `${participant?.identity ?? "unknown"}: ${s}`,
                            ]);
                        } catch (e) {
                            console.error("Failed to decode message:", e);
                        }
                    }
                );

                r.on(RoomEvent.ParticipantConnected, (p: any) =>
                    console.log("Seller: participantConnected", p.identity)
                );
                r.on(RoomEvent.ParticipantDisconnected, (p: any) =>
                    console.log("Seller: participantDisconnected", p.identity)
                );

                r.on(RoomEvent.TrackUnsubscribed, (track: RemoteTrack) => {
                    if (track.kind === "video" && videoRef.current) {
                        try {
                            track.detach(videoRef.current);
                        } catch (e) {
                            console.warn("Viewer: detach track failed", e);
                        }
                    }
                });

                // Attach first remote video (for debug)
                r.on(
                    RoomEvent.TrackSubscribed,
                    (
                        track: RemoteTrack,
                        publication: RemoteTrackPublication,
                        participant: { identity: string }
                    ) => {
                        if (track.kind === "video" && videoRef.current) {
                            try {
                                track.attach(videoRef.current);
                            } catch (e) {
                                console.warn(
                                    "Seller: attach remote video failed",
                                    e
                                );
                            }
                        }
                    }
                );
            } catch (error) {
                console.error("Connection failed:", error);
                toast.error("Failed to connect to the livestream");
            } finally {
                setIsLoading(false);
            }
        };

        connect();

        return () => {
            mounted = false;
            if (room) {
                room.disconnect();
            }
        };
    }, []);

    const sendMessage = async () => {
        if (!room || !msg.trim()) return;
        if (msg.length > 500) {
            toast.error("Message too long");
            return;
        }

        try {
            await room.localParticipant.publishData(
                new TextEncoder().encode(msg),
                { reliable: true }
            );
            setChat((prev) => [...prev, `me: ${msg}`]);
            setMsg("");
            console.log("Sent message");
        } catch (error) {
            toast.error("Failed to send message");
        }
    };

    const broadcastAddToCart = async (productId: string) => {
        if (!room) return;
        if (!productId.match(/^[a-zA-Z0-9_-]+$/)) {
            toast.error("Invalid product ID");
            return;
        }

        try {
            const payload = `CART:add:${productId}`;
            await room.localParticipant.publishData(
                new TextEncoder().encode(payload),
                { reliable: true } // Use DataPublishOptions instead of DataPacket_Kind
            );
            toast.success(`Broadcasted add-to-cart for product ${productId}`);
        } catch (error) {
            toast.error("Failed to broadcast add-to-cart");
        }
    };

    return (
        <div className="p-4">
            <h2 className="text-2xl font-bold">Seller (Publisher)</h2>
            {isLoading && <p>Connecting to livestream...</p>}
            {!isLoading && !isConnected && (
                <p className="text-red-500">Disconnected</p>
            )}
            <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="w-full h-96 bg-black rounded-lg"
            />

            <div className="mt-4">
                <input
                    type="text"
                    placeholder="Type your message..."
                    value={msg}
                    onChange={(e) => setMsg(e.target.value)}
                    onKeyPress={(e) => e.key === "Enter" && sendMessage()}
                    className="border p-2 w-full rounded"
                    disabled={!isConnected}
                />
                <button
                    onClick={sendMessage}
                    className="bg-blue-500 text-white p-2 mt-2 rounded disabled:bg-gray-400"
                    disabled={!isConnected || !msg.trim()}
                >
                    Send
                </button>
                <button
                    onClick={() => broadcastAddToCart("product123")}
                    className="bg-green-500 text-white p-2 mt-2 ml-2 rounded disabled:bg-gray-400"
                    disabled={!isConnected}
                >
                    Broadcast Add Product to Cart
                </button>
            </div>

            <div className="mt-4 border p-2 rounded">
                <h3 className="font-semibold">Chat / Events</h3>
                <div style={{ maxHeight: 200, overflow: "auto" }}>
                    {chat.map((c, i) => (
                        <div key={i} className="py-1">
                            {c}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

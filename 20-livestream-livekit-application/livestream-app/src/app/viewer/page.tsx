"use client";

import React, { useEffect, useRef, useState } from "react";
import {
    Room,
    RemoteTrack,
    RemoteTrackPublication,
    DataPacket_Kind,
    joinLiveKitRoom,
} from "../../lib/livekit";
import toast from "react-hot-toast";
import { RoomEvent } from "livekit-client";

export default function ViewerPage() {
    const videoRef = useRef<HTMLVideoElement>(null);
    const [room, setRoom] = useState<Room | null>(null);
    const [chat, setChat] = useState<string[]>([]);
    const [msg, setMsg] = useState("");
    const [userId] = useState(`viewer-${Math.floor(Math.random() * 1000)}`);
    const [isConnected, setIsConnected] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        let mounted = true;

        const connect = async () => {
            try {
                setIsLoading(true);
                const token = await fetch(
                    `${process.env.NEXT_PUBLIC_API_URL}/api/livekit/token?room=shop1&identity=${userId}&isPublisher=false`
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

                const attachExistingTracks = () => {
                    if (!videoRef.current) return;
                    r.remoteParticipants.forEach((participant) => {
                        participant.trackPublications.forEach(
                            (publication: RemoteTrackPublication) => {
                                if (
                                    publication.track &&
                                    publication.track.kind === "video"
                                ) {
                                    try {
                                        publication.track.attach(
                                            videoRef.current!
                                        );
                                    } catch (e) {
                                        console.warn(
                                            "Viewer: attach existing track failed",
                                            e
                                        );
                                    }
                                }
                            }
                        );
                    });
                };

                attachExistingTracks();

                r.on(RoomEvent.ParticipantConnected, attachExistingTracks);

                r.on(
                    RoomEvent.TrackSubscribed,
                    (
                        track: RemoteTrack,
                        publication: RemoteTrackPublication,
                        participant: { identity: string }
                    ) => {
                        if (track.kind === "video" && videoRef.current) {
                            try {
                                track.attach(videoRef.current!);
                            } catch (e) {
                                console.warn(
                                    "Viewer: attach remote track failed",
                                    e
                                );
                            }
                        }
                    }
                );

                r.on(
                    RoomEvent.DataReceived,
                    async (
                        payload: Uint8Array,
                        participant?: { identity: string }
                    ) => {
                        const s = new TextDecoder().decode(payload);
                        const identity = participant?.identity ?? "unknown";
                        if (s.startsWith("CART:add:")) {
                            const productId = s.split(":")[2];
                            try {
                                await fetch(
                                    `${process.env.NEXT_PUBLIC_API_URL}/api/cart/add?userId=${userId}`,
                                    {
                                        method: "POST",
                                        headers: {
                                            "Content-Type": "application/json",
                                        },
                                        body: JSON.stringify({ productId }),
                                    }
                                );
                                setChat((prev) => [
                                    ...prev,
                                    `${identity} requested add ${productId} for ${userId}`,
                                ]);
                                toast.success(
                                    `Added product ${productId} to cart`
                                );
                            } catch (error) {
                                toast.error("Failed to add to cart");
                            }
                            return;
                        }

                        setChat((prev) => [...prev, `${identity}: ${s}`]);
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
    }, [userId]);

    const sendMessage = async () => {
        if (!room || !msg.trim()) return;
        if (msg.length > 500) {
            toast.error("Message too long");
            return;
        }

        try {
            console.log("Sending message:", msg);
            await room.localParticipant.publishData(
                new TextEncoder().encode(msg),
                {
                    reliable: true,
                }
            );

            console.log(room.localParticipant.identity);

            setChat((prev) => [...prev, `me: ${msg}`]);
            setMsg("");
        } catch (error) {
            toast.error("Failed to send message");
        }
    };

    return (
        <div className="p-4">
            <h2 className="text-2xl font-bold">Viewer</h2>
            {isLoading && <p>Connecting to livestream...</p>}
            {!isLoading && !isConnected && (
                <p className="text-red-500">Disconnected</p>
            )}
            <video
                ref={videoRef}
                autoPlay
                playsInline
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

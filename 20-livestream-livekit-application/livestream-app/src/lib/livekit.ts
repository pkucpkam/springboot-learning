import {
    Room,
    LocalParticipant,
    RemoteTrack,
    RemoteTrackPublication,
    DataPacket_Kind,
    VideoPresets,
} from 'livekit-client';

export async function joinLiveKitRoom(wsUrl: string, token: string): Promise<Room> {
    try {
        if (!wsUrl || !token) {
            throw new Error('WebSocket URL and token are required');
        }

        const room = new Room({
            adaptiveStream: true,

            dynacast: true,

            videoCaptureDefaults: {
                facingMode: 'user',
                resolution: VideoPresets.h1080.resolution,
            },

        });


        // room.prepareConnection(url, token);

        // room
        //     .on(RoomEvent.TrackSubscribed, handleTrackSubscribed)
        //     .on(RoomEvent.TrackUnsubscribed, handleTrackUnsubscribed)
        //     .on(RoomEvent.ActiveSpeakersChanged, handleActiveSpeakerChange)
        //     .on(RoomEvent.Disconnected, handleDisconnect)
        //     .on(RoomEvent.LocalTrackUnpublished, handleLocalTrackUnpublished);


        // try {
        //     await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        // } catch (permError: any) {
        //     throw new Error('Failed to obtain media permissions: ' + permError.message);
        // }

        await room.connect(wsUrl, token);

        if (!room) {
            throw new Error('Failed to create LiveKit room');
        }

        return room;
    } catch (error) {
        console.error('Failed to join LiveKit room:', error);
        throw error; // Let the caller handle the error
    }
}

// Re-export required types and constants
export {
    Room,
    LocalParticipant,
    RemoteTrack,
    RemoteTrackPublication,
    DataPacket_Kind,
};
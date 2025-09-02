# Livestream Platform (LiveKit + Spring Boot + Next.js)

## Introduction

this project includes:

-   **LiveKit Server**: room management, WebRTC connection for livestream.
-   **Backend (Spring Boot)**: token allocation, server logic processing.
-   **Frontend (Next.js)**: user interface, connect to LiveKit to join the room.

---

## ⚙️ System Requirements

-   [Docker](https://docs.docker.com/get-docker/) (easiest way to run LiveKit using Docker)
-   [Java 17+](https://adoptium.net/) (for Spring Boot)
-   [Node.js 18+](https://nodejs.org/) and [pnpm](https://pnpm.io/) or npm/yarn (for Next.js)

---

## 1. Run LiveKit Server

Clone LiveKit project or use Docker:

```bash
  docker compose up -d
```

or

```bash
docker run --rm -it \
-p 7880:7880 \ # HTTP API
-p 7881:7881 \ # WebRTC TCP
-p 7882:7882/udp \ # WebRTC UDP
livekit/livekit-server \
--dev
```

Default:

-   API Key: `devkey`
-   Secret: `secret`

🔗 LiveKit Dashboard: [http://localhost:7880](http://localhost:7880)

---

## 2. Backend (Spring Boot)

### Configuration

In the `application.yml` file:

```yaml
livekit:
    url: "https://localhost:7880" # project URL (livekit project url)
    api:
        key: "devkey"
        secret: "devsecret"
```

### Run the server

```bash
cd Livekit-springboot
./gradlew bootRun
```

Server runs at: `http://localhost:8080`

Example API token issuance:

```
GET http://localhost:8080/api/livekit/token?room=myroom&identity=user1
```

---

## 3. Frontend (Next.js)

### .env

Create `.env.local` file in frontend folder:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_LIVEKIT_WS_URL=ws://localhost:7880
```

### Install dependencies & run

```bash
cd livestream-app
pnpm install
pnpm dev
```

App runs at: `http://localhost:3000`
---

## 🛠️ Activity flow

1. Mở **Next.js frontend**  
   - Seller: [http://localhost:3000/seller](http://localhost:3000/seller)  
   - Viewer: [http://localhost:3000/viewer](http://localhost:3000/viewer)

2. Frontend calls backend (`/api/livekit/token`) to request **AccessToken**.

3. Backend uses LiveKit's API Key/Secret to generate token.

4. Frontend uses that token to join LiveKit room.

5. User can **livestream, watch stream, chat realtime** through LiveKit.


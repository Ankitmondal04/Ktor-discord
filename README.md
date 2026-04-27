# Ktor Based Chat Application

A Discord-like real-time chat server built in **Kotlin** using **Ktor** and **MongoDB**.
Supports multiple chat rooms where users can create rooms, join via unique room ID,
send and receive messages in real-time, and leave rooms whenever they want.

## Features

| Feature | Description |
|---------|-------------|
| **WebSockets** | Persistent bidirectional connections for real-time message broadcasting |
| **REST APIs** | HTTP endpoints for room creation, member management and message history |
| **MongoDB** | NoSQL database for persisting rooms, messages and member-room relationships |
| **Multi-Room Support** | Users can create and join multiple rooms using unique room IDs |
| **Member Management** | Tracks which rooms each member has joined across server restarts |
| **CORS** | Configured for cross-platform access from Android, iOS or Web clients |
| **Status Pages** | Global exception handling with proper HTTP status codes |
| **Content Negotiation** | Automatic JSON serialization and deserialization |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Health check |
| POST | `/room/create?label=` | Create a new room |
| GET | `/allMessages?roomId=` | Get all messages in a room |
| GET | `/member/getAllRoom?username=` | Get all rooms a member is in |
| DELETE | `/room/leave?username=&roomId=` | Leave a room |
| WS | `/chat/?username=&roomId=` | Connect to a room via WebSocket |

## Tech Stack

- **Language** — Kotlin
- **Framework** — Ktor
- **Database** — MongoDB
- **Real-time** — WebSockets
- **Serialization** — kotlinx.serialization

## Running the Project

Make sure MongoDB is running locally on port 27017, then run:

```bash
./gradlew run
```

If the server starts successfully you'll see:
Application started in 0.5 seconds.
Responding at http://0.0.0.0:8080

## Can Be Used As
A ready-to-use backend server for any Android, iOS or Web based chat application.
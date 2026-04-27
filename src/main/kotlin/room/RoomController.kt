package com.example.room

import com.example.data.model.*
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val dataSource: DataSource
){
    private val members = ConcurrentHashMap<String, ConcurrentHashMap<String, MemberSession>>()

    suspend fun onJoin(
        username: String,
        roomId: String,
        sessionId: String,
        socketId: WebSocketSession
    ) {
        val allRooms = dataSource.getAllRoom()
        if (allRooms.none { it.id == roomId }) {
            throw RoomDoesNotExistException()
        }

        val existingMember = dataSource.getMember(username)
        if(existingMember == null) {
            dataSource.insertMember(
                Members(username = username,
                roomId = emptyList()
                )
            )
        }

        //No room creation during joining
        val roomMembers = members.getOrPut(roomId) { ConcurrentHashMap() }

        if(roomMembers.containsKey(username)) throw MemberAlreadyExistedException()

        roomMembers[username] = MemberSession(
            username = username,
            socket = socketId
        )

        dataSource.addRoomToMember(username, roomId)

    }

    suspend fun sendMessage(
        message: String,
        username: String,
        roomId: String
    ) {
        val messageEntity = Message(
            text = message,
            username = username,
            roomId = roomId,
            timeStamp = System.currentTimeMillis()
        )

        // 2. Save to MongoDB
        dataSource.insertMessage(messageEntity)

        // 3. Get all live members in this specific room only
        val roomMembers = members[roomId] ?: return

        // 4. Broadcast to every member in this room
        roomMembers.values.forEach { memberSession ->
            val frameText = Json.encodeToString(Message.serializer(), messageEntity)
            memberSession.socket.send(Frame.Text(frameText))
        }
    }

    fun tryDisconnect(username: String, roomId: String) {
        // 1. Get this room's members
        val roomMembers = members[roomId] ?: return

        // 2. Remove this user from the room
        roomMembers.remove(username)

        // 3. If room is now empty, clean it up from memory
        if (roomMembers.isEmpty()) {
            members.remove(roomId)
        }
    }

    suspend fun getRooms(): List<Room> {
        return dataSource.getAllRoom()
    }

    suspend fun createRoom(label: String): Room {
        // 1. Get all existing rooms
        val existingRooms = dataSource.getAllRoom()

        // 2. Check if name is taken
        if (existingRooms.any { it.label == label }) {
            throw RoomAlreadyExistedException()
        }

        // 3. Create the new room
        val room = Room(label = label)

        // 4. Save to MongoDB
        dataSource.createRoom(room)

        // 5. Return it so the caller gets the roomId
        return room
    }

}
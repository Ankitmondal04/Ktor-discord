package com.example.data.model

interface DataSource {
    suspend fun getAllMessage(): List<Message>

    suspend fun getAllMessagesByRoom(roomId: String): List<Message>

    suspend fun insertMessage(message: Message)

    suspend fun getAllRoom(): List<Room>

    suspend fun getAllRoomByMember(username: String): List<Room>

    suspend fun createRoom(room: Room)

    suspend fun getMember(username: String): Members?

    suspend fun addRoomToMember(username: String, roomId: String)

    suspend fun removerRoomFromMember(username: String, roomId: String)

    suspend fun insertMember(member: Members)
}
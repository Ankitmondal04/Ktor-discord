package com.example.data

import com.example.data.model.DataSource
import com.example.data.model.MemberDoesNotExistException
import com.example.data.model.Members
import com.example.data.model.Message
import com.example.data.model.Room
import com.example.data.model.RoomDoesNotExistException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class DataSourceImpl(
    private val db: MongoDatabase
): DataSource{

    private val messages: MongoCollection<Message> = db.getCollection<Message>("message")
    private val rooms: MongoCollection<Room> = db.getCollection<Room>("room")
    private val members: MongoCollection<Members> = db.getCollection<Members>("member")

    override suspend fun getAllMessage(): List<Message> {
        return messages.find().sort(Sorts.ascending(Message::timeStamp.name)).toList()
    }

    override suspend fun getAllMessagesByRoom(roomId: String): List<Message> {

        val roomExists = rooms.find(
            Filters.eq(Room::id.name, roomId)
        ).firstOrNull()

        if (roomExists == null) {
            throw RoomDoesNotExistException()
        }

        return messages.find(Filters.eq(Message::roomId.name, roomId)).sort(Sorts.ascending(Message::timeStamp.name)).toList()
    }

    override suspend fun getAllRoom(): List<Room> {
        return rooms.find().toList()
    }

    override suspend fun getAllRoomByMember(username: String): List<Room> {
        val user = getMember(username) ?: throw MemberDoesNotExistException()
        return rooms.find(Filters.`in`("_id", user::roomId)).toList()
    }

    override suspend fun addRoomToMember(username: String, roomId: String) {
        members.updateOne(
            Filters.eq(Members::username.name, username),
            Updates.addToSet(Members::roomId.name, roomId)
        )
    }

    override suspend fun removerRoomFromMember(username: String, roomId: String) {
        members.updateOne(
            Filters.eq(Members::username.name, username),
            Updates.pull(Members::roomId.name, roomId)
        )
    }

    override suspend fun getMember(username: String): Members? {
        return members.find(
            Filters.eq(Members::username.name, username)
        ).firstOrNull()
    }

    override suspend fun insertMember(member: Members) {
        members.insertOne(member)
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun createRoom(room: Room) {
        rooms.insertOne(room)
    }

}
package com.example.data.model

import kotlinx.serialization.*
import org.bson.types.ObjectId

@Serializable
data class Room(
    val label: String,
    @SerialName("_id")
    val id: String = ObjectId().toString()
)

class RoomAlreadyExistedException: Exception("This room is already there")

class RoomDoesNotExistException: Exception("This room does not exist")
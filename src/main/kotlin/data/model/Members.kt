package com.example.data.model

import io.ktor.websocket.WebSocketSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Members(
    val username: String,
    val roomId: List<String>,
    @SerialName("_id")
    val id: String = ObjectId().toString(),
)

data class MemberSession(
    val username: String,
    val socket: WebSocketSession
)

class MemberAlreadyExistedException: Exception("The member is already in the room")

class MemberDoesNotExistException: Exception("This member doesn't exist")
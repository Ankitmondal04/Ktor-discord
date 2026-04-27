package com.example.data.model

import kotlinx.serialization.*
import org.bson.types.ObjectId

@Serializable
data class Message(
    val text: String,
    val username: String,
    val roomId: String,
    val timeStamp: Long,
    @SerialName("_id")
    val id: String = ObjectId().toString()
)

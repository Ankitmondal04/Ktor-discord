package com.example

import com.example.data.model.DataSource
import com.example.data.model.MemberDoesNotExistException
import com.example.data.model.RoomAlreadyExistedException
import com.example.data.model.RoomDoesNotExistException
import com.example.room.RoomController
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.sessions.*

fun Application.configureRouting(
    dataSource: DataSource,
    roomController: RoomController
) {
    routing {
        get("/") {
            call.respondText("Server is running...")
        }
        get("/allMessages") {
            val roomId = call.request.queryParameters["roomId"]?:""
            try {
                val messages = dataSource.getAllMessagesByRoom(roomId)
                call.respond(messages)
            } catch (e: RoomDoesNotExistException) {
                call.respond(HttpStatusCode.NotFound, "Room does not exist")
            }
        }
        post("/room/create") {
            val roomLabel = call.request.queryParameters["label"]?:""
            try {
                roomController.createRoom(label = roomLabel)
                call.respond(HttpStatusCode.Created, "Room created successfully")
            } catch (e: RoomAlreadyExistedException) {
                call.respond(HttpStatusCode.Conflict, "Room already exists")
            }
        }
        delete ("/room/leave"){
            val roomId = call.request.queryParameters["roomId"]?:""
            val username = call.request.queryParameters["username"]?:""
            dataSource.removerRoomFromMember(username = username, roomId = roomId)
            call.respond(HttpStatusCode.OK, "Left room successfully")
        }
        get ("/member/getAllRoom"){
            val username = call.request.queryParameters["username"]?:""
            try {
                val rooms = dataSource.getAllRoomByMember(username)
                call.respond(rooms)
            } catch (e: MemberDoesNotExistException) {
                call.respond(HttpStatusCode.NotFound, "Member does not exist")
            }
        }

    }
}
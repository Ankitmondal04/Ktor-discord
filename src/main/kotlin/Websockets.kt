package com.example

import com.example.data.model.MemberAlreadyExistedException
import com.example.data.model.RoomDoesNotExistException
import com.example.room.RoomController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.time.Duration.Companion.seconds

fun Application.configureWebsockets(
    roomController: RoomController
) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing{
        webSocket("/chat/") {
            val username = call.request.queryParameters["username"]?:run{
                return@webSocket
            }
            val roomId = call.request.queryParameters["roomId"]?:run {
                return@webSocket
            }

            try {
                roomController.onJoin(
                    username = username,
                    roomId = roomId,
                    sessionId = this.toString(),
                    socketId = this
                )
            }catch (e: MemberAlreadyExistedException) {
                return@webSocket
            }catch (e: RoomDoesNotExistException) {
                return@webSocket
            }

            try{
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        roomController.sendMessage(
                            username = username,
                            message = frame.readText(),
                            roomId = roomId
                        )
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }finally {
                roomController.tryDisconnect(username, roomId)
            }
        }
    }
}

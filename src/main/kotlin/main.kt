package com.example

import com.example.data.DataSourceImpl
import com.example.data.model.DataSource
import com.example.room.RoomController
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val mongoClient = MongoClient.create("mongodb://127.0.0.1:27017")
    val db = mongoClient.getDatabase("discord_db")

    val dataSource = DataSourceImpl(db)
    val roomController = RoomController(dataSource)

    configureSerialization()
    configureHttp()
    configureSecurity()
    configureStatusPages()
    configureWebsockets(roomController = roomController)
    configureRouting(dataSource = dataSource, roomController = roomController)
}
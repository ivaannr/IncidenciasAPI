package com.example.demo.ws

import com.example.demo.model.ChatMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class BroadcastHandler : TextWebSocketHandler() {

    private val MAPPER = ObjectMapper()
    private val ROOMS: MutableMap<String, MutableList<WebSocketSession>> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val id = getSessionId(session)
        ROOMS.computeIfAbsent(id) { CopyOnWriteArrayList() }.add(session)
        println("Connection established ${session.id}")
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        try {
            val content: ChatMessage = try {
                MAPPER.readValue(message.payload, ChatMessage::class.java)
            } catch (e: Exception) {
                println(e)
                ChatMessage("0", "error", "", "", "")
            } as ChatMessage

            val chatId = getSessionId(session)
            val sessionsInRoom = ROOMS[chatId]

            sessionsInRoom?.forEach { target ->
                if (target.id != session.id && target.isOpen) {
                    target.sendMessage(TextMessage(content.text))
                }
            }
        } catch (e: Exception) {
            e.stackTrace.forEach { traceElement ->
                println(traceElement)
            }
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        println("Session closed: ${session.id}, status: $status")
    }

    private fun getSessionId(session: WebSocketSession): String = session.uri.toString().substring(session.uri.toString().lastIndexOf('/') + 1)
}
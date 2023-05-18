package com.github.bestk.ra.core

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketHelper (private val webSocketListener: WebSocketListener) {

    private lateinit var webSocket: WebSocket

    fun connect(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun send(message: String) {
        webSocket.send(message)
    }

    fun close() {
        webSocket.close(NORMAL_CLOSURE_STATUS, "Closing WebSocket")
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}
package com.github.bestk.ra.core

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.bestk.ra.command.*
import com.github.bestk.ra.constant.Constant
import com.github.bestk.ra.enums.CommandType
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.AccessibilityUtil.globalGoBack
import com.github.bestk.ra.utils.AccessibilityUtil.scrollByXY
import okhttp3.WebSocket
import okhttp3.WebSocketListener


/**
 *无障碍
 */
class RemoteAssistantService : AccessibilityService() {

    var currentPackage = ""
    var currentClass = ""
    private var mLastKnownRoot: AccessibilityNodeInfo? = null
    lateinit var webSocketHelper: WebSocketHelper


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 处理无障碍事件，例如根据事件类型执行相应的操作
        currentPackage = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""
        if (className.contains(currentPackage)) {
            currentClass = className
        }
        mLastKnownRoot = event.source


    }



    override fun onInterrupt() {
        // 处理服务被中断的情况，例如当用户关闭无障碍服务时
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.i("开启无障碍成功")
        // 初始化 controller
        RemoteAssistantController.service = this
        initWebSocket()
    }

    fun initWebSocket() {
        // 创建WebSocket监听器，处理消息和连接状态
        val webSocketListener = initWebSocketListener()
        // 创建并连接WebSocket
        webSocketHelper = WebSocketHelper(webSocketListener)
        webSocketHelper.connect(getUrl())
    }

    private fun getUrl(): String {
        return "${Constant.HOST}?id=${Constant.ROBOT_ID}"
    }

    private fun initWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                // 处理WebSocket连接打开
                ToastUtils.showLong("WebSocket连接成功:$webSocket")
                Constant.WS_STATUS = "WebSocket连接成功:$webSocket"
            }


            override fun onMessage(webSocket: WebSocket, text: String) {
                // 处理收到的消息
                LogUtils.d("服务器消息:$text")
                val remoteCommand: RemoteCommand =
                    GsonUtils.fromJson(text, RemoteCommand::class.java)
                handleRemoteCommand(remoteCommand)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // 处理WebSocket即将关闭
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Constant.WS_STATUS = "WebSocket已关闭:$reason"
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: okhttp3.Response?
            ) {
                // 处理WebSocket连接失败
                ToastUtils.showLong("WebSocket连接失败:${t.message} $response")
                LogUtils.e("WebSocket连接失败:$t $response")
                Constant.WS_STATUS = "WebSocket连接失败:$t $response"
                sleep(3000)
                webSocket.request().url
                webSocketHelper.close()
                webSocketHelper.connect(getUrl())
            }


        }
    }


    fun handleRemoteCommand(
        remoteCommand: RemoteCommand,
        result: MutableList<CommandResult>? = ArrayList()
    ) {

        when (remoteCommand.commandType) {
            CommandType.INTENT -> IntentCommand(remoteCommand, result).execute()
            CommandType.CLICK -> ClickCommand(remoteCommand, result).execute()
            CommandType.BACK -> globalGoBack(this)
            CommandType.SCROLL_UP -> ScrollUpCommand(remoteCommand, result).execute()
            CommandType.SCROLL_DOWN -> ScrollDownCommand(remoteCommand, result).execute()
            CommandType.SWIPE_LEFT -> scrollByXY(
                this,
                remoteCommand.x,
                remoteCommand.y
            )
            CommandType.SWIPE_RIGHT -> scrollByXY(
                this,
                remoteCommand.x,
                remoteCommand.y
            )
            CommandType.GET_INSTALL_PACKAGE -> GetInstalledPkgCommand().execute()
            CommandType.TOAST -> ToastCommand(remoteCommand, result).execute()
            CommandType.SEQUENCE -> SequenceCommand(remoteCommand, result).execute()
            CommandType.SCHEDULE -> ScheduleCommand(remoteCommand, result).execute()
            CommandType.REPORT_RESULT -> ReportResultCommand(remoteCommand, result).execute()
            else -> {
                ToastUtils.showLong("未找到指令:${GsonUtils.toJson(remoteCommand)}")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // 关闭WebSocket连接
        webSocketHelper.close()
    }


}
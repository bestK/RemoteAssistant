package org.yameida.floatwindow

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.*
import org.yameida.floatwindow.databinding.LayoutMenuLeftBinding
import org.yameida.floatwindow.databinding.LayoutMenuLogoBinding
import org.yameida.floatwindow.databinding.LayoutMenuRightBinding

import org.yameida.floatwindow.listener.FloatWindowListener
import org.yameida.floatwindow.listener.OnClickListener

/**
 * Created by Gallon on 2019/9/7.
 */

class DefaultFloatService : BaseFloatWindow(), View.OnClickListener {

    private lateinit var leftBinding: LayoutMenuLeftBinding
    private lateinit var rightBinding: LayoutMenuRightBinding
    private lateinit var logoBinding: LayoutMenuLogoBinding

    //    private var currentStatus = RecordStatusManager.PLAY_STATUS_STOP
    private var active = false
    private var context = Utils.getApp()
    private var manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID_STRING = "907"
    private var floatWindowListener: FloatWindowListener? = null
    var onClickListener: OnClickListener? = null

    inner class DefaultFloatServiceBinder : Binder() {
        fun getService() = this@DefaultFloatService
    }

    init {
        minY = SizeUtils.dp2px(100F)
        maxY = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(150F)
    }

    override fun onBind(intent: Intent?): IBinder? = DefaultFloatServiceBinder()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(CHANNEL_ID_STRING, "float", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(mChannel)
            val notification = Notification.Builder(context, CHANNEL_ID_STRING).build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d(TAG, "onStartCommand: ${intent?.data}")
        show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun show() {
        super.show()
//        FloatWindowManager.isShow = true
        floatWindowListener?.show()
    }

    override fun hide() {
        super.hide()
//        FloatWindowManager.isShow = false
        floatWindowListener?.hide()
    }

    override fun onClick(v: View) {
        LogUtils.v(TAG, "float onClick: ")
        openMenu()
        if (v.id == leftBinding.flWindowBackgroundLeft.id || v.id == rightBinding.flWindowBackgroundRight.id) {
            onClickListener?.onClick(v, -1)
        }
        if (v.id == leftBinding.ivLogoLeft.id || v.id == rightBinding.ivLogoRight.id || v.id == leftBinding.ivLogoLeft2.id || v.id == rightBinding.ivLogoRight2.id) {
            onClickListener?.onClick(v, 0)
        }
        if (v.id == leftBinding.ivStartLeft.id || v.id == rightBinding.ivStartRight.id) {
            onClickListener?.onClick(v, 1)
        }
        if (v.id == leftBinding.ivShotLeft.id || v.id == rightBinding.ivShotRight.id) {
            onClickListener?.onClick(v, 2)
        }
        if (v.id == leftBinding.ivBackLeft.id || v.id == rightBinding.ivBackRight.id) {
            onClickListener?.onClick(v, 3)
        }
        if (v.id == leftBinding.ivSettingsLeft.id || v.id == rightBinding.ivSettingsRight.id) {
            onClickListener?.onClick(v, 4)
        }
        if (v.id == leftBinding.ivResumePauseLeft.id || v.id == rightBinding.ivResumePauseRight.id) {
            onClickListener?.onClick(v, 5)
        }
        if (v.id == leftBinding.ivStopLeft.id || v.id == rightBinding.ivStopRight.id) {
            onClickListener?.onClick(v, 6)
        }
    }

    override fun getLeftView(inflater: LayoutInflater): View {
        leftView = inflater.inflate(R.layout.layout_menu_left, null)
        leftBinding = LayoutMenuLeftBinding.bind(leftView)
        leftBinding.ivLogoLeft.setOnClickListener(this)
        leftBinding.ivLogoLeft2.setOnClickListener(this)
        leftBinding.ivStartLeft.setOnClickListener(this)
        leftBinding.ivShotLeft.setOnClickListener(this)
        leftBinding.ivBackLeft.setOnClickListener(this)
        leftBinding.ivResumePauseLeft.setOnClickListener(this)
        leftBinding.ivStopLeft.setOnClickListener(this)
        leftBinding.ivSettingsLeft.setOnClickListener(this)
        leftBinding.flWindowBackgroundLeft.setOnClickListener(this)
        return leftView
    }

    override fun getRightView(inflater: LayoutInflater): View {
        rightView = inflater.inflate(R.layout.layout_menu_right, null)
        rightBinding = LayoutMenuRightBinding.bind(rightView)
        rightBinding.ivLogoRight.setOnClickListener(this)
        rightBinding.ivLogoRight2.setOnClickListener(this)
        rightBinding.ivStartRight.setOnClickListener(this)
        rightBinding.ivShotRight.setOnClickListener(this)
        rightBinding.ivBackRight.setOnClickListener(this)
        rightBinding.ivResumePauseRight.setOnClickListener(this)
        rightBinding.ivStopRight.setOnClickListener(this)
        rightBinding.ivSettingsRight.setOnClickListener(this)
        rightBinding.flWindowBackgroundRight.setOnClickListener(this)
        return rightView
    }

    override fun getLogoView(inflater: LayoutInflater): View {
        val logoView = inflater.inflate(R.layout.layout_menu_logo, null)
        logoBinding = LayoutMenuLogoBinding.bind(logoView)
        return logoView
    }

    override fun resetLogoViewSize(hintLocation: Int, logoView: View) {
        logoView.clearAnimation()
        logoView.translationX = 0f
        logoView.scaleX = 1f
        logoView.scaleY = 1f
        logoView.alpha = 1f
        logoBinding.tvFloatTime.textSize = 10f
    }

    override fun dragLogoViewOffset(
        logoView: View,
        isDrag: Boolean,
        isResetPosition: Boolean,
        offset: Float
    ) {
        if (isDrag && offset > 0) {
            logoView.scaleX = 1 + offset
            logoView.scaleY = 1 + offset
        } else {
            logoView.translationX = 0f
            logoView.scaleX = 1f
            logoView.scaleY = 1f
        }

        logoView.rotation = offset * 360
    }

    public override fun shrinkLeftLogoView(smallView: View) {
        smallView.translationX = (-smallView.width / 4).toFloat()
        smallView.alpha = 0.35f
        logoBinding.tvFloatTime.textSize = 7f
    }

    public override fun shrinkRightLogoView(smallView: View) {
        smallView.translationX = (smallView.width / 4).toFloat()
        smallView.alpha = 0.35f
        logoBinding.tvFloatTime.textSize = 7f
    }

    public override fun leftViewOpened(leftView: View) {
        val layoutParams =
            leftBinding.flWindowMeasureLeft.layoutParams as FrameLayout.LayoutParams
        leftBinding.flWindowMeasureLeft.measure(0, 0)
        layoutParams.topMargin =
            (params.y - leftBinding.flWindowMeasureLeft.measuredHeight / 2 - leftBinding.ivLogoLeft.measuredHeight / 2)
        leftBinding.flWindowMeasureLeft.layoutParams = layoutParams
//        ToastUtils.showShort("左边的菜单被打开了")
    }

    public override fun rightViewOpened(rightView: View) {
        val layoutParams =
            rightBinding.flWindowMeasureRight.layoutParams as FrameLayout.LayoutParams
        rightBinding.flWindowMeasureRight.measure(0, 0)
        layoutParams.topMargin =
            params.y - rightBinding.flWindowMeasureRight.measuredHeight / 2 - rightBinding.ivLogoRight.measuredHeight / 2
        layoutParams.leftMargin =
            ScreenUtils.getScreenWidth() - rightBinding.flWindowMeasureRight.measuredWidth
        rightBinding.flWindowMeasureRight.layoutParams = layoutParams

//        ToastUtils.showShort("右边的菜单被打开了")
    }

    public override fun leftOrRightViewClosed(smallView: View) {
//        Toast.makeText(context, "菜单被关闭了", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyed() {}
}

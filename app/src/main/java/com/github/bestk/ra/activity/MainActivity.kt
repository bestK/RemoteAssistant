package com.github.bestk.ra.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils
import com.github.bestk.ra.R
import com.github.bestk.ra.constant.Constant
import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.core.RemoteAssistantService
import com.github.bestk.ra.core.SocketIOHelper
import com.github.bestk.ra.core.sleep
import com.github.bestk.ra.databinding.ActivityMainBinding
import com.github.bestk.ra.utils.FloatWindowHelper
import com.github.bestk.ra.utils.PermissionHelper
import com.github.bestk.ra.utils.PermissionPageManagement
import com.hjq.toast.ToastUtils
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import org.yameida.worktool.utils.FlowPermissionHelper


class MainActivity : AppCompatActivity() {

    companion object {
        fun enterActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    private var viewBinding: ActivityMainBinding? = null


    private var goToSetting = false

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding!!.root)

        initViewData()

        initListener()

        initWebSocket()

    }

    override fun onResume() {
        super.onResume()
        initViewData()
    }

    private fun initViewData() {
        viewBinding?.swAccessibility?.isChecked = isAccessibilityServiceEnabled()
        viewBinding?.tvWsStatus?.text = Constant.WS_STATUS
        viewBinding!!.tvHost.text = Constant.HOST
        viewBinding!!.etRobotId.setText(Constant.ROBOT_ID)
        viewBinding!!.swOverlay.isChecked = Settings.canDrawOverlays(Utils.getApp())
        Constant.PACKAGE_NAME = "com.github.bestk.ra"
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun initListener() {
        viewBinding!!.swAccessibility.setOnCheckedChangeListener { _, isChecked ->
            val enabled = isAccessibilityServiceEnabled()
            LogUtils.d("无障碍开启状态： $enabled")
            if (isChecked) {
                if (Constant.ROBOT_ID.isBlank()) {
                    ToastUtils.showLong("请先填写并保存链接号~")
                } else {
                    openAccessibilitySettings()
                }

            } else {
                if (PermissionHelper.isAccessibilitySettingOn()) {
                    viewBinding!!.swAccessibility.isChecked = true
                    openAccessibilitySettings()
                }
            }
        }

        viewBinding!!.tvHost.setOnClickListener {
            showSelectHostDialog()
        }
        viewBinding!!.tvHost.setOnLongClickListener {
            showInputHostDialog()
            true
        }
        viewBinding!!.btSave.setOnClickListener {
            val robotId = viewBinding!!.etRobotId.text.toString().trim()
            if (robotId.isBlank()) {
                ToastUtils.showLong("链接号不能为空！")
            } else {
                Constant.ROBOT_ID = robotId
                ToastUtils.showLong("保存成功")
            }

        }
        initOverlaysListener()
    }

    private fun initOverlaysListener() {
        viewBinding!!.swOverlay.setOnCheckedChangeListener { _, isChecked ->
            LogUtils.i("sw_overlay onCheckedChanged: $isChecked")
            if (isChecked) {
                try {
                    if (!Settings.canDrawOverlays(Utils.getApp())) {
                        ToastUtils.showLong("请打开悬浮窗权限")
                        sleep(1500)
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        startActivity(intent)
                    }
                } catch (_: Throwable) {
                }
            } else {
                if (Settings.canDrawOverlays(Utils.getApp()) && FlowPermissionHelper.canBackgroundStart(
                        Utils.getApp()
                    )
                ) {
                    viewBinding!!.swOverlay.isChecked = true
                    PermissionPageManagement.goToSetting(this)
                }
            }
        }
        if (Settings.canDrawOverlays(Utils.getApp())
            && FlowPermissionHelper.canBackgroundStart(Utils.getApp())
        ) {
            FloatWindowHelper.showWindow()
        }
    }

    private fun initWebSocket() {
        try {
            RemoteAssistantController.service.initWebSocket()
        } catch (_: Exception) {
        }
    }


    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName =
            ComponentName(this, RemoteAssistantService::class.java).flattenToString()
        val enabledServicesSetting = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (enabledServicesSetting.isNullOrEmpty()) {
            return false
        }
        val colonSplitRegex = TextUtils.SimpleStringSplitter(':')
        colonSplitRegex.setString(enabledServicesSetting)

        while (colonSplitRegex.hasNext()) {
            val componentNameString = colonSplitRegex.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
                ?.flattenToString()
            if (enabledService != null && enabledService == serviceName) {
                return true
            }
        }
        return false
    }


    private fun showSelectHostDialog() {
        val hostList = SPUtils.getInstance().getStringSet("host_list", mutableSetOf(Constant.HOST))
        if (hostList.isNotEmpty()) {
            val hostArray = hostList.toTypedArray()
            QMUIDialog.CheckableDialogBuilder(this)
                .setTitle(getString(R.string.host_list))
                .addItems(hostArray) { dialog, which ->
                    Constant.HOST = hostArray[which]
                    viewBinding?.tvHost?.text = hostArray[which]
                    dialog.dismiss()

                }
                .setCheckedIndex(hostList.indexOf(Constant.HOST))
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog)
                .show()
        }
    }

    private fun showInputHostDialog() {
        ToastUtils.showLong("请输入专线网络")
        val builder = QMUIDialog.EditTextDialogBuilder(this)
        builder.setTitle(getString(R.string.tip))
            .setPlaceholder(getString(R.string.input_new_host))
            .setDefaultText(viewBinding?.tvHost?.text)
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .addAction(getString(R.string.delete)) { dialog, index ->
                val hostList =
                    SPUtils.getInstance().getStringSet("host_list", mutableSetOf(Constant.HOST))
                if (hostList.size > 1) {
                    hostList.remove(Constant.HOST)
                    Constant.HOST = hostList.elementAt(0)
                    viewBinding?.tvHost?.text = Constant.HOST
                    SPUtils.getInstance().put("host_list", hostList)
                    dialog.dismiss()
                } else {
                    ToastUtils.showLong("至少保留一个host！")
                }
            }
            .addAction(getString(R.string.cancel)) { dialog, index -> dialog.dismiss() }
            .addAction(getString(R.string.add)) { dialog, index ->
                val text = builder.editText.text
                if (text != null && text.isNotEmpty()) {
                    if (text.matches("ws{1,2}://[^/]+.*".toRegex())) {
                        val hostList = SPUtils.getInstance()
                            .getStringSet("host_list", mutableSetOf(Constant.HOST))
                        hostList.add(text.toString())
                        SPUtils.getInstance().put("host_list", hostList)
                        Constant.HOST = text.toString()
                        viewBinding?.tvHost?.text = text

                        dialog.dismiss()
                    } else {
                        ToastUtils.showLong("格式异常！")
                    }
                } else {
                    ToastUtils.showLong("请勿为空！")
                }
            }
            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show()
    }
}
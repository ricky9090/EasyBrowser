package ricky.easybrowser.page.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ricky.easybrowser.R
import ricky.easybrowser.utils.SharedPreferencesUtils

class SettingDialogKt : DialogFragment() {

    private lateinit var noPictureMode: CheckBox
    private lateinit var exitApp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View = inflater.inflate(R.layout.layout_setting_dialog, container, false)

        val back: ImageView = dialogView.findViewById(R.id.nav_back)
        back.visibility = View.INVISIBLE
        val forward: ImageView = dialogView.findViewById(R.id.nav_forward)
        forward.visibility = View.INVISIBLE
        val home: ImageView = dialogView.findViewById(R.id.nav_home)
        home.visibility = View.INVISIBLE
        val tab: ImageView = dialogView.findViewById(R.id.nav_show_tabs)
        tab.visibility = View.INVISIBLE

        val linearLayout: LinearLayout = dialogView.findViewById(R.id.nav_bar_linear)
        linearLayout.setOnClickListener {
            dismiss()
        }

        noPictureMode = dialogView.findViewById(R.id.no_picture_mode)
        noPictureMode.setOnCheckedChangeListener { buttonView, isChecked ->
            val sp: SharedPreferences? = SharedPreferencesUtils.getSettingSP(context)
            sp?.let {
                val editor: SharedPreferences.Editor = it.edit()
                if (isChecked) {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, true)
                } else {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false)
                }
                editor.apply()
            }
        }

        exitApp = dialogView.findViewById(R.id.exit_app)
        exitApp.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        return dialogView
    }

    override fun onResume() {
        super.onResume()
        // 设置对话框在屏幕底部
        val param: WindowManager.LayoutParams? = dialog?.window?.attributes
        param?.let {
            it.gravity = Gravity.BOTTOM
            dialog?.window?.attributes = it
        }

        val sp: SharedPreferences? = SharedPreferencesUtils.getSettingSP(context)
        sp?.let {
            noPictureMode.isChecked = it.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false)
        }
    }
}
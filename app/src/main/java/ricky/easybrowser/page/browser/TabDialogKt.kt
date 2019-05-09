package ricky.easybrowser.page.browser

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ricky.easybrowser.R

class TabDialogKt : DialogFragment() {

    lateinit var tabCacheManager: TabCacheManager

    lateinit var tabRecyclerView: RecyclerView
    lateinit var tabQuickViewAdapter: TabQuickViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View = inflater.inflate(R.layout.layout_tab_dialog, container, false)
        val back: ImageView = dialogView.findViewById(R.id.nav_back)
        back.visibility = View.INVISIBLE
        val forward: ImageView = dialogView.findViewById(R.id.nav_forward)
        forward.visibility = View.INVISIBLE
        val home: ImageView = dialogView.findViewById(R.id.nav_home)
        home.visibility = View.INVISIBLE
        val setting: ImageView = dialogView.findViewById(R.id.nav_setting)
        setting.visibility = View.INVISIBLE

        val linearLayout: LinearLayout = dialogView.findViewById(R.id.nav_bar_linear)
        linearLayout.setOnClickListener {
            dismiss()
        }

        tabRecyclerView = dialogView.findViewById(R.id.tab_list_recyclerview)
        tabRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        tabQuickViewAdapter = TabQuickViewAdapter(context)
        tabQuickViewAdapter.attachToBrwoserTabs(tabCacheManager)
        tabQuickViewAdapter.listener = object : TabQuickViewAdapter.OnTabClickListener {
            override fun onTabClick(info: TabCacheManager.TabInfo) {
                tabCacheManager.switchToTab(info)
                dismiss()
            }

            override fun onTabClose(info: TabCacheManager.TabInfo) {
                tabCacheManager.closeTab(info)
                dismiss()
            }

            override fun onAddTab() {
                tabCacheManager.addNewTab(getString(R.string.new_tab_welcome))
                dismiss()
            }
        }
        tabRecyclerView.adapter = tabQuickViewAdapter

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
        tabQuickViewAdapter.notifyDataSetChanged()
    }


}
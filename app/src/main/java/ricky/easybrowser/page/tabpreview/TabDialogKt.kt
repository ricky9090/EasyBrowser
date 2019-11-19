package ricky.easybrowser.page.tabpreview

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ricky.easybrowser.R
import ricky.easybrowser.entity.bo.TabInfo
import ricky.easybrowser.page.browser.IBrowser

class TabDialogKt : DialogFragment() {

    var tabViewSubject: TabQuickViewContract.Subject? = null
    private var browser: IBrowser? = null

    lateinit var tabRecyclerView: RecyclerView
    var tabQuickViewAdapter: TabQuickViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)

        if (context is IBrowser) {
            browser = context as IBrowser
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View = inflater.inflate(R.layout.layout_tab_dialog, container, false)

        val foldButton: ImageView = dialogView.findViewById(R.id.nav_fold)
        foldButton.setOnClickListener {
            dismiss()
        }

        tabRecyclerView = dialogView.findViewById(R.id.tab_list_recyclerview)
        tabRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        tabQuickViewAdapter = TabQuickViewAdapter(context)
        tabQuickViewAdapter?.attachToSubject(tabViewSubject)
        tabQuickViewAdapter?.listener = object : TabQuickViewAdapter.OnTabClickListener {
            override fun onTabClick(info: TabInfo) {
                browser?.provideTabController()?.onTabSelected(info)
                dismiss()
            }

            override fun onTabClose(info: TabInfo) {
                browser?.provideTabController()?.onTabClose(info)
                dismiss()
            }

            override fun onAddTab() {
                var info = TabInfo()
                info.title = context?.resources?.getString(R.string.new_tab_welcome)
                info.tag = "" + System.currentTimeMillis()
                browser?.provideTabController()?.onTabCreate(info, false)
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
        tabQuickViewAdapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabRecyclerView.adapter = null
        tabRecyclerView.layoutManager = null
        tabQuickViewAdapter?.listener = null
        tabQuickViewAdapter?.detachSubject()
        tabQuickViewAdapter = null
        browser = null
        tabViewSubject = null
    }
}
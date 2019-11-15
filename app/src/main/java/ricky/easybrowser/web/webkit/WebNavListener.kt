package ricky.easybrowser.web.webkit

import android.content.Context
import android.view.View
import ricky.easybrowser.R
import ricky.easybrowser.page.browser.IBrowser
import ricky.easybrowser.widget.BrowserNavBar

class WebNavListener(context: Context?): BrowserNavBar.OnNavClickListener {

    var mContext = context

    override fun onItemClick(itemView: View) {
        val isBrowserController = mContext is IBrowser
        if (!isBrowserController) {
            return
        }
        val browser = mContext as IBrowser
        when (itemView.id) {
            R.id.nav_back -> browser.provideNavController().goBack()
            R.id.nav_forward -> browser.provideNavController().goForward()
            R.id.nav_home -> browser.provideNavController().goHome()
            R.id.nav_show_tabs -> browser.provideNavController().showTabs()
            R.id.nav_setting -> browser.provideNavController().showSetting()
        }
    }
}
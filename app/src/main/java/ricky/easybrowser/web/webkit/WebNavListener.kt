package ricky.easybrowser.web.webkit

import android.content.Context
import android.view.View
import ricky.easybrowser.R
import ricky.easybrowser.common.BrowserConst
import ricky.easybrowser.contract.IBrowser
import ricky.easybrowser.widget.BrowserNavBar

class WebNavListener(context: Context?): BrowserNavBar.OnNavClickListener {

    var mContext = context

    override fun onItemClick(itemView: View) {
        val isBrowserController = mContext is IBrowser
        if (!isBrowserController) {
            return
        }
        val browser = mContext as IBrowser
        val navController = browser.provideBrowserComponent(BrowserConst.NAVIGATION_COMPONENT)
                as IBrowser.INavController
        when (itemView.id) {
            R.id.nav_back -> navController.goBack()
            R.id.nav_forward -> navController.goForward()
            R.id.nav_home -> navController.goHome()
            R.id.nav_show_tabs -> navController.showTabs()
            R.id.nav_setting -> navController.showSetting()
        }
    }
}
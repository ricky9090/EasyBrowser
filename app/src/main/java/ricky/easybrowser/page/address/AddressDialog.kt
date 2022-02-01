package ricky.easybrowser.page.address

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ricky.easybrowser.R
import ricky.easybrowser.common.BrowserConst
import ricky.easybrowser.contract.IBrowser
import ricky.easybrowser.utils.StringUtils


class AddressDialog : DialogFragment() {

    private var browser: IBrowser? = null

    var currentUrl: String? = null

    var addressUrl: EditText? = null
    var gotoButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
        if (context is IBrowser) {
            browser = context as IBrowser
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View = inflater.inflate(R.layout.layout_address_bar_dialog, container, false)

        val param: WindowManager.LayoutParams? = dialog?.window?.attributes
        param?.let {
            it.gravity = Gravity.TOP
            dialog?.window?.attributes = it
        }

        addressUrl = dialogView.findViewById(R.id.address_url)
        if (StringUtils.isNotEmpty(currentUrl)) {
            addressUrl?.setText(currentUrl)
        }

        gotoButton = dialogView.findViewById(R.id.goto_button)
        gotoButton?.setOnClickListener {
            // browser loadurl
            val url: String? = addressUrl?.editableText?.toString()
            if (StringUtils.isValidUrl(url)) {
                val tabController = browser?.provideBrowserComponent(BrowserConst.TAB_COMPONENT)
                        as? IBrowser.ITabController
                tabController?.onTabLoadUrl(url)
            } else {
                Toast.makeText(context, getText(R.string.warn_invalid_url), Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }

        return dialogView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gotoButton?.setOnClickListener(null)
        browser = null
    }
}
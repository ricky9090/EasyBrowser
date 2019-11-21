package ricky.easybrowser.web.webkit

import android.content.Context
import android.webkit.WebResourceRequest
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ricky.easybrowser.EasyApplication
import ricky.easybrowser.common.Const
import ricky.easybrowser.entity.dao.WebSite
import ricky.easybrowser.entity.dao.WebSiteDao
import ricky.easybrowser.utils.EasyLog

fun captureWebSiteFavicon(context: Context, request: WebResourceRequest): Unit {
    val targetUrl = request.url ?: return
    val targetUrlString = targetUrl.toString()
    EasyLog.i(Const.LOG_TAG, "request url: $targetUrlString")
    if (!targetUrlString.endsWith("favicon.ico", true)) {
        return
    }

    var siteUrl = targetUrlString.dropLast(12)
    val application = context.applicationContext ?: return
    val dps = Observable.create<Unit> {
        EasyLog.i(Const.LOG_TAG, "insert favicon on thread ${Thread.currentThread().name}")
        val easyApplication: EasyApplication = application as EasyApplication
        val siteQuery = easyApplication.daoSession.webSiteDao.queryBuilder()
                .where(WebSiteDao.Properties.SiteUrl.eq(siteUrl))
                .build()
        val targetSite: WebSite? = siteQuery.unique()
        targetSite?.let {
            it.favicoUrl = targetUrlString
            easyApplication.daoSession.webSiteDao.update(it)
        }
    }.subscribeOn(Schedulers.io())
            .subscribe()

}
package ricky.easybrowser.page.history;

import android.content.Context;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.entity.dao.DaoSession;
import ricky.easybrowser.entity.dao.History;
import ricky.easybrowser.entity.dao.HistoryDao;

public class HistoryPresenterImpl implements HistoryContract.Presenter {

    private Context mContext;
    private HistoryContract.View view;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public HistoryPresenterImpl(Context mContext, HistoryContract.View v) {
        this.mContext = mContext;
        this.view = v;
    }

    @Override
    public void getHistory(final int pageNo, final int pageSize) {
        Disposable dps = Observable.create(new ObservableOnSubscribe<List<History>>() {

            @Override
            public void subscribe(ObservableEmitter<List<History>> emitter) throws Exception {
                final EasyApplication application = (EasyApplication) mContext.getApplicationContext();
                DaoSession daoSession = application.getDaoSession();
                Query<History> historyEntityQuery = daoSession.getHistoryDao().queryBuilder()
                        .offset(pageNo * pageSize)  // 总偏移 = 页数 * 分页大小
                        .limit(pageSize)
                        .orderDesc(HistoryDao.Properties.Id)
                        .build();
                List<History> result = historyEntityQuery.list();
                emitter.onNext(result);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<History>>() {
                    @Override
                    public void accept(List<History> historyEntities) throws Exception {
                        // handle result
                        if (historyEntities == null || historyEntities.size() == 0) {
                            view.showEmptyResult();
                        } else {
                            view.showHistory(historyEntities);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // handle error
                    }
                });
        mDisposable.add(dps);
    }

    @Override
    public void onDestroy() {
        mDisposable.clear();
        mContext = null;
        view = null;
    }
}

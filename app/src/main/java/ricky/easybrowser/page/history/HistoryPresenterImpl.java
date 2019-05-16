package ricky.easybrowser.page.history;

import android.content.Context;

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
import ricky.easybrowser.entity.DaoSession;
import ricky.easybrowser.entity.HistoryEntity;

public class HistoryPresenterImpl implements HistoryContract.Presenter {

    private Context mContext;
    private HistoryContract.View view;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public HistoryPresenterImpl(Context mContext, HistoryContract.View v) {
        this.mContext = mContext;
        this.view = v;
    }

    @Override
    public void getHistory() {
        Disposable dps = Observable.create(new ObservableOnSubscribe<List<HistoryEntity>>() {

            @Override
            public void subscribe(ObservableEmitter<List<HistoryEntity>> emitter) throws Exception {
                final EasyApplication application = (EasyApplication) mContext.getApplicationContext();
                DaoSession daoSession = application.getDaoSession();
                List<HistoryEntity> result = daoSession.getHistoryEntityDao().loadAll();
                emitter.onNext(result);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<HistoryEntity>>() {
                    @Override
                    public void accept(List<HistoryEntity> historyEntities) throws Exception {
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

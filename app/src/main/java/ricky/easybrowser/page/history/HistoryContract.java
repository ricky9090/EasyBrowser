package ricky.easybrowser.page.history;

import java.util.List;

import ricky.easybrowser.entity.HistoryEntity;

public interface HistoryContract {

    interface View {
        void showHistory(List<HistoryEntity> result);
        void showEmptyResult();
    }

    interface Presenter {
        void getHistory();
        void onDestroy();
    }
}

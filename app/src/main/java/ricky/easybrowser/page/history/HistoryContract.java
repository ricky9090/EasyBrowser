package ricky.easybrowser.page.history;

import java.util.List;

import ricky.easybrowser.entity.dao.History;

public interface HistoryContract {

    interface View {
        void showHistory(List<History> result);
        void showEmptyResult();
    }

    interface Presenter {
        void getHistory(int pageNo, int pageSize);
        void onDestroy();
    }
}

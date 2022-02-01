package ricky.easybrowser.contract;

import java.util.List;

import ricky.easybrowser.entity.dao.History;

public interface IHistory {

    interface View {
        void showHistory(List<History> result);
        void showEmptyResult();
    }

    interface Presenter {
        void getHistory(int pageNo, int pageSize);
        void onDestroy();
    }
}

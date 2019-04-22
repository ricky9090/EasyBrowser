package ricky.easybrowser.page.browser;

public interface QuickViewUpdateContract {

    interface Subject {
        void attach(Observer observer);
    }

    interface Observer {
        void updateQuickView();
        void addNewTab();
    }
}

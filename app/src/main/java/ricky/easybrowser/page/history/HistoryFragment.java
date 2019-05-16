package ricky.easybrowser.page.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.entity.HistoryEntity;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    private HistoryContract.Presenter presenter;

    public HistoryFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_common_list, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        // TODO 实现分页加载
        swipeRefreshLayout.setEnabled(false);
        recyclerView = rootView.findViewById(R.id.content_list);

        adapter = new HistoryAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        if (getContext() != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        }

        presenter = new HistoryPresenterImpl(getContext(), this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showHistory(List<HistoryEntity> result) {
        adapter.appendDataList(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyResult() {

    }

    private void loadData() {
        if (presenter == null) {
            return;
        }
        presenter.getHistory();
    }
}

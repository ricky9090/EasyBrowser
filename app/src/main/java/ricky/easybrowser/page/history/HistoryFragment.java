package ricky.easybrowser.page.history;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.common.Const;
import ricky.easybrowser.entity.HistoryEntity;
import ricky.easybrowser.page.browser.TabInfo;
import ricky.easybrowser.widget.ptr.PtrLayout;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private PtrLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    private HistoryContract.Presenter presenter;

    private int pageNo = 0;
    private int pageSize = Const.PAGE_SIZE_20;

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
        swipeRefreshLayout.setHasMore(true);
        swipeRefreshLayout.setOnLoadListener(new PtrLayout.OnLoadListener() {
            @Override
            public void onLoadMore() {
                pageNo++;
                loadData(pageNo, pageSize);
            }

            @Override
            public void onRefresh() {
                adapter.clearDataList();
                adapter.notifyDataSetChanged();
                pageNo = 0;
                loadData(pageNo, pageSize);
            }
        });
        recyclerView = rootView.findViewById(R.id.content_list);

        adapter = new HistoryAdapter(getContext());
        adapter.setItemClickListener(new HistoryAdapter.OnHistoryItemClickListener() {
            @Override
            public void onHistoryItemClick(HistoryEntity entity) {
                Uri uri = null;
                try {
                    uri = Uri.parse(entity.getUrl());
                } catch (Exception e) {
                    uri = null;
                }
                if (uri == null) {
                    return;
                }
                TabInfo info = new TabInfo();
                info.setTitle(entity.getTitle());
                info.setUri(uri);
                info.setTag(System.currentTimeMillis() + "");
                Intent resultData = new Intent();
                resultData.putExtra(Const.Key.TAB_INFO, info);

                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK, resultData);
                    getActivity().finish();
                }
            }
        });
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
        loadData(pageNo, pageSize);
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
        swipeRefreshLayout.setRefreshing(false);
        if (result == null || result.size() < pageSize) {
            swipeRefreshLayout.loadFinish(false, false);
        } else {
            swipeRefreshLayout.loadFinish(false, true);
        }
        adapter.appendDataList(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyResult() {

    }

    private void loadData(int _pageNo, int _pageSize) {
        if (presenter == null) {
            return;
        }
        presenter.getHistory(_pageNo, _pageSize);
    }
}

package ricky.easybrowser.widget.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PtrLayout extends SwipeRefreshLayout {

    public int pageSize = 20;
    public boolean hasMore = false;
    public boolean loading = false;

    private RecyclerView mRecyclerView;

    private View loadMoreView;

    private OnLoadListener mOnLoadListener;

    public PtrLayout(Context context) {
        super(context);
    }

    public PtrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        findRecyclerView();
        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!loading && mOnLoadListener != null) {
                    mOnLoadListener.onRefresh();
                }
            }
        });
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        throw new RuntimeException("USE PtrLayout::setOnLoadListener INSTEAD!!!");
    }

    private void findRecyclerView() {
        if (getChildCount() > 0) {
            View target = getChildAt(0);
            if (target instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) target;
                mRecyclerView.clearOnScrollListeners();
                mRecyclerView.addOnScrollListener(new PtrOnScrollListener());
            }
        }
    }

    public OnLoadListener getOnLoadListener() {
        return mOnLoadListener;
    }

    /**
     * 设置加载监听器
     */
    public void setOnLoadListener(OnLoadListener ltn) {
        this.mOnLoadListener = ltn;
    }

    public View getLoadMoreView() {
        return loadMoreView;
    }

    /**
     * 设置加载更多对应自定义View
     * @param loadMoreView 用于显示加载更多文字/图片的View
     */
    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int size) {
        this.pageSize = size;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * 设置是否还有更多数据，仅当有更多数据时，加载回调函数才会生效
     */
    public void setHasMore(boolean more) {
        this.hasMore = more;
    }

    private boolean isLoading() {
        return loading;
    }

    private void setLoading(boolean isLoading) {
        this.loading = isLoading;
    }

    /**
     * 加载完毕后，设置数据状态
     */
    public void loadFinish(boolean emptyResult, boolean more) {
        loading = false;
        hasMore = more;
        if (loadMoreView != null) {
            loadMoreView.setVisibility(View.GONE);
        }
    }

    class PtrOnScrollListener extends RecyclerView.OnScrollListener {

        RecyclerView.LayoutManager layoutManager;
        int lastVisibleItem = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (recyclerView.getAdapter() == null) {
                lastVisibleItem = -1;
                return;
            }
            if (!loading
                    && (lastVisibleItem + 1) == mRecyclerView.getAdapter().getItemCount()) {
                if (loading || !hasMore) {
                    return;
                }

                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadMore();
                    if (loadMoreView != null) {
                        loadMoreView.setVisibility(View.VISIBLE);
                    }
                    loading = true;
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getAdapter() == null) {
                lastVisibleItem = -1;
                return;
            }
            if (layoutManager == null) {
                layoutManager = recyclerView.getLayoutManager();
            }

            if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] spanItem = ((StaggeredGridLayoutManager) layoutManager).findLastCompletelyVisibleItemPositions(null);
                lastVisibleItem = spanItem[0];
            }
        }
    }

    /**
     * 列表的加载监听器
     */
    public interface OnLoadListener {
        /**
         * 加载更多回调函数
         */
        void onLoadMore();

        /**
         * 下拉刷新回调函数
         */
        void onRefresh();
    }
}
package ricky.easybrowser.widget.ptr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.BuildConfig;

public abstract class PtrAdapter<ENTITY, HOLDER extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter implements View.OnClickListener {

    private int headerCount;
    private int footerCount;

    public static final int VIEW_TYPE_HEADER = 1000;
    public static final int VIEW_TYPE_CONTENT = 1001;
    public static final int VIEW_TYPE_FOOTER = 1002;

    private Context mContext;
    private List<ENTITY> dataList;

    private OnListItemClickListener onListItemClickListener;

    public PtrAdapter(Context ctx, List<ENTITY> datas) {
        this(ctx, datas, 0, 0);
    }

    public PtrAdapter(Context ctx, @Nullable List<ENTITY> datas, int headerNum, int footerNum) {
        mContext = ctx;
        dataList = datas;
        headerCount = headerNum;
        footerCount = footerNum;
    }

    /**
     * 生成列表项的ViewHolder
     *
     * @param ctx      Context对象
     * @param parent   parent视图
     * @param viewType 视图类型
     * @return ViewHolder对象
     */
    public abstract HOLDER getViewHolder(Context ctx, ViewGroup parent, int viewType);

    public abstract int getHeaderLayoutId();

    public abstract int getFooterLayoutId();

    /**
     * 填充ViewHolder数据
     *
     * @param holder   对应项的ViewHolder
     * @param datas    全部数据列表
     * @param position 填充项在列表中的位置
     */
    public abstract void fillData(HOLDER holder, List<ENTITY> datas, int position);

    public void fillHeader(RecyclerView.ViewHolder holder) {

    }

    public void fillFooter(RecyclerView.ViewHolder holder) {

    }

    public RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent) {
        View header = LayoutInflater.from(mContext).inflate(getHeaderLayoutId(), parent, false);
        return new HeaderHolder(header);
    }

    public RecyclerView.ViewHolder getFooterViewHolder(ViewGroup parent) {
        View footer = LayoutInflater.from(mContext).inflate(getFooterLayoutId(), parent, false);
        return new FooterHolder(footer);
    }


    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return getHeaderViewHolder(parent);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return getFooterViewHolder(parent);
        } else {
            HOLDER holder = getViewHolder(mContext, parent, viewType);
            holder.itemView.setOnClickListener(this);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int dataSize;
        if (dataList == null) {
            dataSize = 0;
        } else {
            dataSize = dataList.size();
        }

        try {
            if (position >= 0 && position < headerCount) {  // header position
                fillHeader(holder);
            } else if (position >= headerCount && position < (dataSize + headerCount)) {  // content position
                int realPosition = position - headerCount;
                if (dataList != null && realPosition < dataList.size()) {
                    fillData((HOLDER) holder, dataList, realPosition);
                    holder.itemView.setTag(realPosition);
                }
            } else if (position >= (dataSize + headerCount) && footerCount > 0) {  // footer position
                fillFooter(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if (dataList != null) {
            return getContentSize() + headerCount + footerCount;
        } else {
            return headerCount + footerCount;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerCount > 0 && position < headerCount) {
            return VIEW_TYPE_HEADER;
        } else if (footerCount > 0 && position > headerCount + getContentSize() - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_CONTENT;
        }
    }

    @Override
    public void onClick(View v) {
        if (onListItemClickListener != null) {
            try {
                final int position = (int) v.getTag();
                onListItemClickListener.onItemClick(v, position);
            } catch (Exception e) {

            }

        }
    }

    public Context getAdapterContext() {
        return mContext;
    }

    public List<ENTITY> getDataList() {
        return dataList;
    }

    public void setDataList(List<ENTITY> list) {
        this.dataList = list;
    }

    /**
     * 在已有列表后添加数据
     *
     * @param dataListToAdd
     */
    public void addDataList(List<ENTITY> dataListToAdd) {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }

        this.dataList.addAll(dataListToAdd);
    }

    /**
     * 清空数据列表
     */
    public void clearDataList() {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
            return;
        }

        this.dataList.clear();
    }

    public int getContentSize() {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        return dataList.size();
    }

    public OnListItemClickListener getOnListItemClickListener() {
        return onListItemClickListener;
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    /**
     * 列表项点击监听器
     */
    public interface OnListItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

}

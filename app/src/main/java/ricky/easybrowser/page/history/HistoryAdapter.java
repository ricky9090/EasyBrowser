package ricky.easybrowser.page.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.R;
import ricky.easybrowser.entity.HistoryEntity;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {


    private Context mContext;
    private final List<HistoryEntity> dataList = new ArrayList<>();

    private OnHistoryItemClickListener itemClickListener;

    public HistoryAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_history_item, parent, false);
        HistoryViewHolder vh = new HistoryViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (dataList.size() <= position) {
            return;
        }
        final HistoryEntity entity = dataList.get(position);
        if (entity == null) {
            return;
        }
        holder.title.setText(entity.getTitle());
        holder.url.setText(entity.getUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onHistoryItemClick(entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void clearDataList() {
        dataList.clear();
    }

    public void appendDataList(List<HistoryEntity> list) {
        dataList.addAll(list);
    }

    public void setItemClickListener(OnHistoryItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView url;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.history_title);
            url = itemView.findViewById(R.id.history_url);
        }
    }

    interface OnHistoryItemClickListener {
        void onHistoryItemClick(HistoryEntity entity);
    }
}

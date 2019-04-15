package ricky.easybrowser.page.browsertab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ricky.easybrowser.R;

public class TabQuickViewAdapter extends RecyclerView.Adapter<TabQuickViewAdapter.TabQuickViewHolder> {

    private Context context;
    private BrowserTabAdapter attachedAdapter;
    private OnTabClickListener listener;

    public TabQuickViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TabQuickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_tab_item, parent, false);
        TabQuickViewHolder holder = new TabQuickViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TabQuickViewHolder holder, final int position) {
        if (attachedAdapter == null || attachedAdapter.getTabList() == null) {
            return ;
        }
        String url = attachedAdapter.getTabList().get(position);

        holder.siteTitle.setText(url);
        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachedAdapter == null || attachedAdapter.getTabList() == null) {
                    return;
                }

                attachedAdapter.removeTabAt(position);
                notifyTabDataSetChanged();
            }
        });

        holder.siteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabClick(position);
                }
            }
        });
    }

    public void attachToBrwoserTabAdapter(BrowserTabAdapter target) {
        attachedAdapter = target;
    }

    public void notifyTabDataSetChanged() {
        notifyDataSetChanged();
        if (attachedAdapter != null) {
            attachedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (attachedAdapter == null || attachedAdapter.getTabList() == null) {
            return 0;
        }
        return attachedAdapter.getTabList().size();
    }

    public OnTabClickListener getListener() {
        return listener;
    }

    public void setListener(OnTabClickListener listener) {
        this.listener = listener;
    }

    static class TabQuickViewHolder extends RecyclerView.ViewHolder {

        TextView siteTitle;
        Button closeButton;

        public TabQuickViewHolder(@NonNull View itemView) {
            super(itemView);

            siteTitle = itemView.findViewById(R.id.item_title);
            closeButton = itemView.findViewById(R.id.item_close_button);
        }
    }

    interface OnTabClickListener {
        void onTabClick(int position);
    }
}

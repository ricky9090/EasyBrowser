package ricky.easybrowser.page.frontpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.R;
import ricky.easybrowser.entity.dao.DaoSession;
import ricky.easybrowser.entity.dao.WebSite;

public class SiteAdapterV2 extends RecyclerView.Adapter<SiteAdapterV2.SiteViewHolder> {

    private final List<WebSite> dataList = new ArrayList<>();
    private Context context;

    private SiteAdapterV2.OnSiteItemClickListener listener;

    public SiteAdapterV2(Context context) {
        this.context = context;
    }

    public final List<WebSite> getDataList() {
        return dataList;
    }

    public void clearDataList() {
        dataList.clear();
    }

    public void appendDataList(List<WebSite> list) {
        dataList.addAll(list);
    }

    public SiteAdapterV2.OnSiteItemClickListener getListener() {
        return listener;
    }

    public void setListener(SiteAdapterV2.OnSiteItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SiteAdapterV2.SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_item, parent, false);
        SiteAdapterV2.SiteViewHolder holder = new SiteViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SiteAdapterV2.SiteViewHolder holder, int position) {
        if (getItemCount() <= position) {
            return;
        }

        final WebSite entity = dataList.get(position);
        holder.title.setText(entity.getSiteName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSiteItemClick(entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class SiteViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;

        public SiteViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
        }
    }

    public interface OnSiteItemClickListener {
        void onSiteItemClick(WebSite webSite);
    }

    public static List<WebSite> getTestDataList(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return new ArrayList<>();
        }
        final EasyApplication application = (EasyApplication) context.getApplicationContext();
        DaoSession daoSession = application.getDaoSession();
        List<WebSite> dbList = daoSession.getWebSiteDao().loadAll();
        if (dbList == null || dbList.size() <= 0) {
            return new ArrayList<>();
        }
        List<WebSite> list = new ArrayList<>();
        list.addAll(dbList);

        return list;
    }
}
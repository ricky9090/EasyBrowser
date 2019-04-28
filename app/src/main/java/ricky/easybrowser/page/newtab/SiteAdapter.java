package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ricky.easybrowser.EasyApplication;
import ricky.easybrowser.R;
import ricky.easybrowser.entity.DaoSession;
import ricky.easybrowser.entity.SiteEntity;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteViewHolder> {

    public static final int DEFAULT_PAGE_SIZE = 6;

    private final List<SiteEntity> dataList = new ArrayList<>();
    private Context context;
    private int pageSize = DEFAULT_PAGE_SIZE;

    private OnSiteItemClickListener listener;

    public SiteAdapter(Context context, int pageSize) {
        this.context = context;
        this.pageSize = pageSize;
    }

    public List<SiteEntity> getDataList() {
        return dataList;
    }

    public void clearDataList() {
        dataList.clear();
    }

    public void appendDataList(List<SiteEntity> list) {
        dataList.addAll(list);
    }

    public OnSiteItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnSiteItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_horizontal_page, parent, false);
        SiteViewHolder holder = new SiteViewHolder(itemView, pageSize);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SiteViewHolder holder, int position) {
        if (getItemCount() <= position) {
            return;
        }

        int start = position * pageSize;
        for (int i = start; (i - start) < pageSize; i++) {
            int indexOffset = i - start;
            SiteItemHolder siteHolder = holder.getItemHolder(indexOffset);

            if (i >= dataList.size()) {
                siteHolder.icon.setVisibility(View.GONE);
                siteHolder.title.setVisibility(View.GONE);
                siteHolder.itemView.setOnClickListener(null);
            } else {
                final SiteEntity siteEntity = dataList.get(i);
                siteHolder.title.setText(siteEntity.getSiteName());
                siteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onSiteItemClick(siteEntity);
                        }
                        Toast.makeText(context, "click item " + siteEntity.getSiteName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int dataCount = dataList.size();
        if (dataCount <= pageSize) {
            return 1;
        } else {
            return (int) Math.ceil((double) dataCount / pageSize);
        }
    }

    static class SiteViewHolder extends RecyclerView.ViewHolder {

        TableRow row1;
        TableRow row2;
        int pageSize;

        public SiteViewHolder(@NonNull View itemView, int pageSize) {
            super(itemView);
            row1 = itemView.findViewById(R.id.row_1);
            row2 = itemView.findViewById(R.id.row_2);
            this.pageSize = pageSize;

            for (int i = 0; i < pageSize; i++) {
                View item = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_grid_item, row1, false);
                ImageView icon = item.findViewById(R.id.item_image);
                TextView title = item.findViewById(R.id.item_title);
                if (i < pageSize / 2) {
                    row1.addView(item);
                } else {
                    row2.addView(item);
                }
            }
        }

        public View getItemView(int i) {
            if (pageSize <= 0) {
                return null;
            }
            if (i < pageSize / 2) {
                return row1.getChildAt(i);
            } else {
                int offset = i - pageSize / 2;
                return row2.getChildAt(offset);
            }
        }

        public SiteItemHolder getItemHolder(int i) {
            View view = getItemView(i);
            if (view == null) {
                return null;
            }

            SiteItemHolder holder = new SiteItemHolder(view);
            return holder;
        }
    }

    static class SiteItemHolder {
        View itemView;

        ImageView icon;
        TextView title;

        public SiteItemHolder(View itemView) {
            this.itemView = itemView;

            icon = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
        }
    }

    public interface OnSiteItemClickListener {
        void onSiteItemClick(SiteEntity siteEntity);
    }

    public static List<SiteEntity> getTestDataList(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            return new ArrayList<>();
        }
        final EasyApplication application = (EasyApplication) context.getApplicationContext();
        DaoSession daoSession = application.getDaoSession();
        List<SiteEntity> dbList = daoSession.getSiteEntityDao().loadAll();
        if (dbList == null || dbList.size() <= 0) {
            return new ArrayList<>();
        }
        List<SiteEntity> list = new ArrayList<>();
        list.addAll(dbList);

        return list;
    }
}

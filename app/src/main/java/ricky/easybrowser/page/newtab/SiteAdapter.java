package ricky.easybrowser.page.newtab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ricky.easybrowser.R;

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
            View view = holder.getItemView(indexOffset);
            ImageView icon = view.findViewById(R.id.item_image);
            TextView title = view.findViewById(R.id.item_title);

            if (i >= dataList.size()) {
                icon.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                view.setOnClickListener(null);
            } else {
                final SiteEntity siteEntity = dataList.get(i);
                title.setText(siteEntity.getSiteName());
                view.setOnClickListener(new View.OnClickListener() {
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

        public SiteViewHolder(@NonNull View itemView) {
            super(itemView);
            row1 = itemView.findViewById(R.id.row_1);
            row2 = itemView.findViewById(R.id.row_2);
        }

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
    }

    interface OnSiteItemClickListener {
        void onSiteItemClick(SiteEntity siteEntity);
    }

    public static List<SiteEntity> getTestDataList() {
        List<SiteEntity> list = new ArrayList<>();

        /*for (int i = 0; i < 28; i ++) {
            SiteEntity entity = new SiteEntity();
            entity.setSiteName(i + ".Google");
            entity.setSiteUrl("www.google.com");
            list.add(entity);
        }*/
        SiteEntity entity1 = new SiteEntity();
        entity1.setSiteName("1.Baidu");
        entity1.setSiteUrl("www.baidu.com");

        SiteEntity entity2 = new SiteEntity();
        entity2.setSiteName("2.Google");
        entity2.setSiteUrl("www.google.com");

        SiteEntity entity3 = new SiteEntity();
        entity3.setSiteName("3.掘金");
        entity3.setSiteUrl("juejin.im");

        SiteEntity entity4 = new SiteEntity();
        entity4.setSiteName("4.V2ex");
        entity4.setSiteUrl("v2ex.com");

        SiteEntity entity5 = new SiteEntity();
        entity5.setSiteName("5.Twitter");
        entity5.setSiteUrl("www.twitter.com");

        SiteEntity entity6 = new SiteEntity();
        entity6.setSiteName("6.网易");
        entity6.setSiteUrl("www.163.com");

        SiteEntity entity7 = new SiteEntity();
        entity7.setSiteName("7.QQ");
        entity7.setSiteUrl("www.qq.com");

        SiteEntity entity8 = new SiteEntity();
        entity8.setSiteName("8.微博");
        entity8.setSiteUrl("weibo.com");

        list.add(entity1);
        list.add(entity2);
        list.add(entity3);
        list.add(entity4);
        list.add(entity5);
        list.add(entity6);
        list.add(entity7);
        list.add(entity8);

        return list;
    }
}

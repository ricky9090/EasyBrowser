package ricky.easybrowser.page.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import ricky.easybrowser.R;

public class HistoryFragment extends Fragment {


    Toolbar toolbar;

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
        toolbar = rootView.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_36dp);
        toolbar.setTitle(R.string.history);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof HistoryActivity) {
                    ((HistoryActivity) getContext()).onBackPressed();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

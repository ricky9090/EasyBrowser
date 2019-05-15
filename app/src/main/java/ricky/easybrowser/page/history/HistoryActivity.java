package ricky.easybrowser.page.history;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ricky.easybrowser.R;

public class HistoryActivity extends AppCompatActivity {

    private static final String F_TAG = "history_fragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, new HistoryFragment(), F_TAG).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

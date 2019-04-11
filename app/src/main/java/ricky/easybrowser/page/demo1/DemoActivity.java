package ricky.easybrowser.page.demo1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import ricky.easybrowser.R;
import ricky.easybrowser.page.demo1.ui.demo.DemoFragment;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DemoFragment.newInstance())
                    .commitNow();
        }
    }
}

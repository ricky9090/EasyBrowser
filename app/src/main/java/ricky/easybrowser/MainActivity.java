package ricky.easybrowser;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ricky.easybrowser.page.browser.BrowserActivity;
import ricky.easybrowser.page.demo2.DemoActivityV2;

public class MainActivity extends AppCompatActivity {

    Button tabButton;
    Button demo2PageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabButton = findViewById(R.id.new_tab_button);
        tabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BrowserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        demo2PageButton = findViewById(R.id.demo2_activity_button);
        demo2PageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DemoActivityV2.class);
                startActivity(intent);
            }
        });
    }
}

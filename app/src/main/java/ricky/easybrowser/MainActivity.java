package ricky.easybrowser;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ricky.easybrowser.page.browser.BrowserActivity;

public class MainActivity extends AppCompatActivity {

    Button tabButton;

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
            }
        });
    }
}

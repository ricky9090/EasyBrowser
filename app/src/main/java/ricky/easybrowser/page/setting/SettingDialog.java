package ricky.easybrowser.page.setting;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ricky.easybrowser.R;

public class SettingDialog extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.layout_setting_dialog, container, false);
        ImageView back = dialogView.findViewById(R.id.nav_back);
        back.setVisibility(View.INVISIBLE);
        ImageView foward = dialogView.findViewById(R.id.nav_foward);
        foward.setVisibility(View.INVISIBLE);
        ImageView home = dialogView.findViewById(R.id.nav_home);
        home.setVisibility(View.INVISIBLE);
        ImageView tab = dialogView.findViewById(R.id.nav_show_tabs);
        tab.setVisibility(View.INVISIBLE);

        ImageView settingBtn = dialogView.findViewById(R.id.nav_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialogView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 设置对话框在屏幕底部
        WindowManager.LayoutParams param = getDialog().getWindow().getAttributes();
        param.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(param);
    }
}

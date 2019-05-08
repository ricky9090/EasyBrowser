package ricky.easybrowser.page.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ricky.easybrowser.R;
import ricky.easybrowser.utils.SharedPreferencesUtils;

public class SettingDialog extends DialogFragment {

    private CheckBox noPictureMode;
    private TextView exitApp;

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
        ImageView forward = dialogView.findViewById(R.id.nav_forward);
        forward.setVisibility(View.INVISIBLE);
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

        noPictureMode = dialogView.findViewById(R.id.no_picture_mode);
        noPictureMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = SharedPreferencesUtils.getSettingSP(getContext());
                if (sp == null) {
                    return;
                }
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, true);
                } else {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false);
                }
                editor.apply();
            }
        });

        exitApp = dialogView.findViewById(R.id.exit_app);
        exitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process.killProcess(Process.myPid());
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

        SharedPreferences sp = SharedPreferencesUtils.getSettingSP(getContext());
        if (sp.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false)) {
            noPictureMode.setChecked(true);
        } else {
            noPictureMode.setChecked(false);
        }
    }
}

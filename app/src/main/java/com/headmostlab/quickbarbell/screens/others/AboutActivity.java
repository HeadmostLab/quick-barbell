package com.headmostlab.quickbarbell.screens.others;

import android.os.Bundle;
import android.widget.TextView;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.AppUtils;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_about);

        TextView textViewVersion = findViewById(R.id.version);
        textViewVersion.setText(getString(R.string.aa_version, AppUtils.getVersionName(this)));
    }
}

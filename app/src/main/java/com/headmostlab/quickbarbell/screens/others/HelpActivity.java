package com.headmostlab.quickbarbell.screens.others;

import android.os.Bundle;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_help);
    }
}

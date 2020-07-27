package com.headmostlab.quickbarbell;

import android.os.Bundle;

import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.screens.selectbarbell.SelectBarbellFragment;
import com.headmostlab.quickbarbell.screens.selectsystemunit.SelectSystemUnitFragment;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Inject
    SettingsModel settingsModel;

    @Inject
    DiskDao diskDao;

    @Inject
    BarDao barDao;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        App.getInstance().getAppComponent().inject(this);

        setTheme(settingsModel.getTheme().themeId);

        setContentView(R.layout.activity_main);

        if (state != null) {
            return;
        }

        if (!isDbPrepared()) {
            startFragment(new SelectSystemUnitFragment(), true);
        } else {
            startFragment(new SelectBarbellFragment(), false);
        }

    }

    private boolean isDbPrepared() {
        boolean prepared = settingsModel.isDbPrepared();
        if (!prepared) {
            prepared = barDao.hasOne() != 0 || diskDao.hasOne() != 0;
            if (prepared) {
                settingsModel.prepareDb();
            }
        }
        return prepared;
    }

    private void startFragment(Fragment fragment, boolean animated) {
        final FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        if (animated) {
            tran.setCustomAnimations(R.animator.fragment_animation_delayed_enter, R.animator.fragment_animation_exit);
        }
        tran.replace(R.id.rootLayout, fragment).commit();
    }

}

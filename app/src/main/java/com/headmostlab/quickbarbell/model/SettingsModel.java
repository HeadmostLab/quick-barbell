package com.headmostlab.quickbarbell.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.di.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class SettingsModel {
    final SharedPreferences sharedPreferences;

    private final static String PARAM_UNIT = "UNIT";
    private final static String PARAM_HIDE_HELP = "HELP";
    private final static String PARAM_IS_DB_PREPARED = "IS_DB_PREPARED";
    private final static String PARAM_IS_SET_SEVERAL_BARBELL_SETS = "IS_SEVERAL_BARBELL_SETS";
    private final static String PARAM_THEME = "THEME";

    @Inject
    public SettingsModel(Application app) {
        sharedPreferences = app.getSharedPreferences(app.getString(R.string.prefSettings), Context.MODE_PRIVATE);
    }

    public MeasurementUnit getUnit() {
        return MeasurementUnit.valueOf(sharedPreferences.getString(PARAM_UNIT, MeasurementUnit.KILOGRAM.name()));
    }

    public void setUnit(MeasurementUnit unit) {
        sharedPreferences.edit().putString(PARAM_UNIT, unit.name()).apply();
    }

    public boolean isHelpHidden() {
        return sharedPreferences.getBoolean(PARAM_HIDE_HELP, false);
    }

    public void hideHelp(boolean show) {
        sharedPreferences.edit().putBoolean(PARAM_HIDE_HELP, show).apply();
    }

    public boolean isDbPrepared() {
        return sharedPreferences.getBoolean(PARAM_IS_DB_PREPARED, false);
    }

    public void prepareDb() {
        sharedPreferences.edit().putBoolean(PARAM_IS_DB_PREPARED, true).apply();
    }

    public boolean isSeveralBarbellSets() {
        return sharedPreferences.getBoolean(PARAM_IS_SET_SEVERAL_BARBELL_SETS, false);
    }

    public void setSeveralBarbellSets(boolean isSeveral) {
        sharedPreferences.edit().putBoolean(PARAM_IS_SET_SEVERAL_BARBELL_SETS, isSeveral).apply();
    }

    public Themes getTheme() {
        return Themes.valueOf(sharedPreferences.getString(PARAM_THEME, Themes.GRAY.name()));
    }

    public void setTheme(Themes themeKind) {
        sharedPreferences.edit().putString(PARAM_THEME, themeKind.name()).apply();
    }

}

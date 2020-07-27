package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards;

import android.app.Activity;

public class ActivityCard extends SimpleCard {

    private Class activityClass;
    private String description;

    public ActivityCard(int icon, Class<? extends Activity> activityClass, String description) {
        super(icon);
        this.activityClass = activityClass;
        this.description = description;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activity) {
        this.activityClass = activity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

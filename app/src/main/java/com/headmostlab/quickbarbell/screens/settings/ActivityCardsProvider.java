package com.headmostlab.quickbarbell.screens.settings;

import android.content.Context;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.screens.others.AboutActivity;
import com.headmostlab.quickbarbell.screens.barinventory.BarInventoryActivity;
import com.headmostlab.quickbarbell.screens.diskinventory.DiskInventoryActivity;
import com.headmostlab.quickbarbell.screens.others.HelpActivity;
import com.headmostlab.quickbarbell.screens.others.OtherSettingsActivity;
import com.headmostlab.quickbarbell.screens.weightcalculation.WeightCalculationActivity;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.ActivityCard;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ActivityCardsProvider {

    private List<ActivityCard> cards;

    @Inject
    public ActivityCardsProvider() {
        Context context = App.getInstance();
        cards = new ArrayList<>();
        addCards(context);
    }

    private void addCards(Context context) {
        cards.add(new ActivityCard(R.drawable.card_ez_bar_2, BarInventoryActivity.class, context.getString(R.string.as_bar_inventory)));
        cards.add(new ActivityCard(R.drawable.card_disk_2, DiskInventoryActivity.class, context.getString(R.string.as_disk_inventory)));
        cards.add(new ActivityCard(R.drawable.card_settings_2, OtherSettingsActivity.class, context.getString(R.string.as_other_settings)));
        cards.add(new ActivityCard(R.drawable.card_weight_calc_2, WeightCalculationActivity.class, context.getString(R.string.as_weight_search)));
        cards.add(new ActivityCard(R.drawable.card_help_2, HelpActivity.class, context.getString(R.string.as_help)));
        cards.add(new ActivityCard(R.drawable.card_info_2, AboutActivity.class, context.getString(R.string.as_about)));
    }

    public List<ActivityCard> getCards() {
        return cards;
    }

    public ActivityCard find(String className) {
        for (ActivityCard card : cards) {
            if (card.getActivityClass().getCanonicalName().equals(className)) {
                return card;
            }
        }
        return null;
    }

}

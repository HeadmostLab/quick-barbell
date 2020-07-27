package com.headmostlab.quickbarbell.screens.others;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.Themes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtherSettingsActivity extends AppCompatActivity {

    @BindView(R.id.unitGroup)
    RadioGroup unitRadioGroup;

    @BindView(R.id.switchHideHelp)
    SwitchCompat switchHideHelp;

    @BindView(R.id.severalBarbellsCheckBox)
    MaterialCheckBox severalBarbellsCheckBox;

    @BindView(R.id.themePickerRecyclerView)
    RecyclerView themePicker;

    private SettingsModel settingsModel;

    private Themes curTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsModel = App.getInstance().getSettingsModel();

        curTheme = settingsModel.getTheme();

        setTheme(curTheme.themeId);

        setContentView(R.layout.activity_other_settings);
        ButterKnife.bind(this);

        setUpWidgets();
    }

    private void setUpWidgets() {
        setUpRadioGroup();
        setUpSwitchHideHelp();
        setUpSeveralBarbellsCheckBox();
        setUpThemePickerRecyclerView();
    }

    private void setUpSwitchHideHelp() {
        switchHideHelp.setTypeface(ResourcesCompat.getFont(this, R.font.comic));
        switchHideHelp.setChecked(settingsModel.isHelpHidden());
        switchHideHelp.setOnCheckedChangeListener((buttonView, isChecked) -> settingsModel.hideHelp(isChecked));
    }

    private void setUpRadioGroup() {
        unitRadioGroup.check(settingsModel.getUnit() == MeasurementUnit.POUND ?
                R.id.radioButtonPound : R.id.radioButtonKilogram);

        unitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonKilogram:
                    settingsModel.setUnit(MeasurementUnit.KILOGRAM);
                    break;
                case R.id.radioButtonPound:
                    settingsModel.setUnit(MeasurementUnit.POUND);
                    break;
            }
        });
    }

    private void setUpSeveralBarbellsCheckBox() {
        severalBarbellsCheckBox.setChecked(settingsModel.isSeveralBarbellSets());
        severalBarbellsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> settingsModel.setSeveralBarbellSets(isChecked));
    }

    private void setUpThemePickerRecyclerView() {
        themePicker.setAdapter(new ColorPickerAdapter());
        themePicker.setLayoutManager(new LinearLayoutManager(this));
        themePicker.scrollToPosition(curTheme.ordinal());
    }

    class ColorPickerAdapter extends ListAdapter<Themes, ColorPickerAdapter.ColorViewHolder> {

        private Themes[] themes;

        protected ColorPickerAdapter() {
            super(new DiffUtil.ItemCallback<Themes>() {

                @Override
                public boolean areItemsTheSame(@NonNull Themes oldTheme, @NonNull Themes newTheme) {
                    return oldTheme.equals(newTheme);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Themes oldTheme, @NonNull Themes newTheme) {
                    return oldTheme.equals(newTheme);
                }
            });

            themes = Themes.values();
        }

        @NonNull
        @Override
        public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ColorViewHolder(inflater.inflate(R.layout.settings_color_picker_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
            final Themes theme = themes[position];
            holder.textView.setText(App.getInstance().getString(theme.nameId));
            holder.textView.setTextColor(theme.color);
            holder.colorView.setCardBackgroundColor(theme.color);
            holder.bgView.setBackgroundColor(curTheme.equals(theme) ? 0x10ffffff : 0x00000000);
            holder.theme = theme;
        }

        @Override
        public int getItemCount() {
            return themes.length;
        }

        class ColorViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.itemView)
            View itemView;

            @BindView(R.id.textView)
            TextView textView;

            @BindView(R.id.colorView)
            MaterialCardView colorView;

            @BindView(R.id.bgView)
            View bgView;

            Themes theme;

            ColorViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick(R.id.itemView)
            void click() {
                settingsModel.setTheme(theme);
                recreate();
            }
        }

    }


}

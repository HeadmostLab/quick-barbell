package com.headmostlab.quickbarbell.di;

import android.app.Application;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.MainActivity;
import com.headmostlab.quickbarbell.screens.barinventory.BarInventoryActivity;
import com.headmostlab.quickbarbell.screens.barinventory.BarInventoryViewModelComponent;
import com.headmostlab.quickbarbell.screens.diskinventory.DiskInventoryActivity;
import com.headmostlab.quickbarbell.screens.diskinventory.DiskInventoryViewModelComponent;
import com.headmostlab.quickbarbell.screens.selectassembledbar.SelectAssembledBarActivity;
import com.headmostlab.quickbarbell.screens.selectassembledbar.SelectAssembledBarViewModelComponent;
import com.headmostlab.quickbarbell.screens.selectbarbell.SelectBarbellFragment;
import com.headmostlab.quickbarbell.screens.selectbarbell.SelectBarbellViewModelComponent;
import com.headmostlab.quickbarbell.screens.selectsystemunit.SelectSystemUnitViewModelComponent;
import com.headmostlab.quickbarbell.screens.selectweight.SelectWeightActivity;
import com.headmostlab.quickbarbell.screens.selectweight.SelectWeightViewModelComponent;
import com.headmostlab.quickbarbell.screens.settings.SettingsViewModelComponent;
import com.headmostlab.quickbarbell.screens.showweights.ShowWeightsActivity;
import com.headmostlab.quickbarbell.screens.showweights.ShowWeightsViewModelComponent;
import com.headmostlab.quickbarbell.screens.weightcalculation.WeightCalculationViewModelComponent;
import com.headmostlab.quickbarbell.screens.weighthistory.WeightHistoryViewModelComponent;

import dagger.BindsInstance;
import dagger.Component;

@ApplicationScope
@Component(
        modules = {
                AppModule.class,
                DaosModule.class,
                XmlModule.class,
                BillingModule.class,
                SelectBarbellViewModelComponent.InstallationModule.class,
                SelectWeightViewModelComponent.InstallationModule.class,
                SelectAssembledBarViewModelComponent.InstallationModule.class,
                ShowWeightsViewModelComponent.InstallationModule.class,
                SettingsViewModelComponent.InstallationModule.class,
                BarInventoryViewModelComponent.InstallationModule.class,
                DiskInventoryViewModelComponent.InstallationModule.class,
                WeightCalculationViewModelComponent.InstallationModule.class,
                WeightHistoryViewModelComponent.InstallationModule.class,
                SelectSystemUnitViewModelComponent.InstallationModule.class
        })
public interface AppComponent {

    void inject(App app);

    void inject(MainActivity activity);
    void inject(SelectWeightActivity activity);
    void inject(SelectAssembledBarActivity activity);
    void inject(ShowWeightsActivity activity);
    void inject(BarInventoryActivity activity);
    void inject(DiskInventoryActivity activity);

    void inject(SelectBarbellFragment fragment);

    SelectBarbellViewModelComponent.Factory selectBarbellPresenterFactory();
    SelectWeightViewModelComponent.Factory selectWeightPresenterFactory();
    SelectAssembledBarViewModelComponent.Factory selectAssembledBarPresenterFactory();
    ShowWeightsViewModelComponent.Factory showWeightsPresenterFactory();
    SettingsViewModelComponent.Factory settingsPresenterFactory();
    BarInventoryViewModelComponent.Factory barInventoryFactory();
    DiskInventoryViewModelComponent.Factory diskInventoryFactory();
    WeightCalculationViewModelComponent.Factory weightCalculationFactory();
    WeightHistoryViewModelComponent.Factory weightHistoryFactory();
    SelectSystemUnitViewModelComponent.Factory selectSystemUnitFactory();

    @Component.Builder
    interface Builder {

        @ApplicationScope
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}

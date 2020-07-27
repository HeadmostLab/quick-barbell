package com.headmostlab.quickbarbell.model.di;

import android.app.Application;

import com.headmostlab.quickbarbell.business.AssembledBarsProviderTest;
import com.headmostlab.quickbarbell.business.BunchCalculatorTest;
import com.headmostlab.quickbarbell.TestApp;
import com.headmostlab.quickbarbell.di.AppComponent;
import com.headmostlab.quickbarbell.di.ApplicationScope;
import com.headmostlab.quickbarbell.di.DaosModule;
import com.headmostlab.quickbarbell.di.XmlModule;

import dagger.BindsInstance;
import dagger.Component;

@ApplicationScope
@Component(modules = {TestAppModule.class, XmlModule.class, DaosModule.class})
public interface TestAppComponent extends AppComponent {

    void inject(TestApp testApp);
    void inject(BunchCalculatorTest test);
    void inject(AssembledBarsProviderTest test);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TestAppComponent.Builder application(Application application);
        TestAppComponent build();
    }

}

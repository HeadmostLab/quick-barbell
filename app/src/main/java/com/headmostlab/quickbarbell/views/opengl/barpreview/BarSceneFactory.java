package com.headmostlab.quickbarbell.views.opengl.barpreview;

import android.content.Context;

import com.headmostlab.openglengine.hl.HLRect;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.AssembledBarScene;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.CurlyBarScene;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.DoubleDumbbellBarScene;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.DumbbellBarScene;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.StraightBarScene;

public class BarSceneFactory {
    static AssembledBarScene create(AssembledBar assembledBar, HLRect viewPort, Context context, Loader loader, int lightColor) {
        switch (assembledBar.getBar().getBarType()) {
            case STRAIGHT:
                return new StraightBarScene(assembledBar, viewPort, context, loader, lightColor);
            case CURLY:
                return new CurlyBarScene(assembledBar, viewPort, context, loader, lightColor);
            case DUMBBELL:
                return new DumbbellBarScene(assembledBar, viewPort, context, loader, lightColor);
            case DOUBLE_DUMBBELL:
                return new DoubleDumbbellBarScene(assembledBar, viewPort, context, loader, lightColor);
        }
        throw new RuntimeException("Bar type is not supported");
    }
}

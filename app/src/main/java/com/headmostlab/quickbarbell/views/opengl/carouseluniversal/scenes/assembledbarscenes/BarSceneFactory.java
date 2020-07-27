package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.assembledbarscenes;

import android.content.Context;

import com.headmostlab.quickbarbell.business.BarTypes;

public class BarSceneFactory {
    public static AssembledBarCardScene create(BarTypes type, Context context) {
        switch (type) {
            case STRAIGHT:
                return new StraightBarCardScene(context);
            case CURLY:
                return new CurlyBarCardScene(context);
            case DUMBBELL:
                return new DumbbellBarCardScene(context);
            case DOUBLE_DUMBBELL:
                return new DoubleDumbbellBarCardScene(context);
        }
        throw new RuntimeException("Bar type is not supported");
    }
}

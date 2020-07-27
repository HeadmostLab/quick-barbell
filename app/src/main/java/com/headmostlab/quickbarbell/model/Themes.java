package com.headmostlab.quickbarbell.model;

import com.headmostlab.quickbarbell.R;

import androidx.annotation.StyleRes;

public enum Themes {

    GRAY   (R.string.colorGray,   0xffB0B0B0, R.style.BaseTheme_Colored),
    BLUE   (R.string.colorBlue,   0xff0091ea, R.style.BaseTheme_Colored_Blue),
    RED    (R.string.colorRed,    0xffea0000, R.style.BaseTheme_Colored_Red),
    GREEN  (R.string.colorGreen,  0xff00a150, R.style.BaseTheme_Colored_Green),
    YELLOW (R.string.colorYellow, 0xffebbc00, R.style.BaseTheme_Colored_Yellow),
    PINK   (R.string.colorPink,   0xffeb009c, R.style.BaseTheme_Colored_Pink),
    PURPLE (R.string.colorPurple, 0xff9c00eb, R.style.BaseTheme_Colored_Purple),
    ORANGE (R.string.colorOrange, 0xffeb8900, R.style.BaseTheme_Colored_Orange);

    public int color;
    public int themeId;
    public int nameId;

    Themes(int nameId, int color, @StyleRes int themeId) {
        this.nameId = nameId;
        this.color = color;
        this.themeId = themeId;
    }
}

package com.headmostlab.quickbarbell.utils;

import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;

import java.util.List;

public class AssembledBarUtils {

    public static boolean findSame(List<AssembledBar> bars, AssembledBar newBar) {
        for (AssembledBar bar : bars) {
            if (! bar.getBar().equals(newBar.getBar())) {
                continue;
            }
            if (! DiskUtils.isEquals(bar.getLeftDisks(), newBar.getLeftDisks())) {
                continue;
            }
            if (! DiskUtils.isEquals(bar.getRightDisks(), newBar.getRightDisks())) {
                continue;
            }
            return true;
        }

        return false;
    }
}

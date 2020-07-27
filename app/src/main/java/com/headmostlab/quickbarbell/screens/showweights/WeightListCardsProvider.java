package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.BarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WeightListCardsProvider {

    private AssembledBar assembledBar;
    private List<Card> cards;
    private boolean isShowDiskUnit;
    private boolean isShowBarUnit;

    public WeightListCardsProvider() {
        this.cards = new ArrayList<>();
    }

    public void setAssembledBar(AssembledBar assembledBar, boolean isShowBarUnit, boolean isShowDiskUnit) {
        this.isShowBarUnit = isShowBarUnit;
        this.isShowDiskUnit = isShowDiskUnit;

        if (assembledBar != this.assembledBar) {
            this.assembledBar = assembledBar;
            createCards();
        }
    }

    private void createCards() {
        cards.clear();
        addBarCard(assembledBar, cards);
        addDiskCards(assembledBar, cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    private void addBarCard(AssembledBar assembledBar, List<Card> cards) {
        Bar bar = assembledBar.getBar();
        BarTypes barType = bar.getBarType() == BarTypes.DOUBLE_DUMBBELL ? BarTypes.DUMBBELL : bar.getBarType();
        cards.add(new BarCard(barType, bar.getWeight(), bar.getUnit(), isShowBarUnit));
    }

    private void addDiskCards(AssembledBar assembledBar, List<Card> cards) {
        List<Disk> disks = new LinkedList<>();
        disks.addAll(assembledBar.getLeftDisks());
        disks.addAll(assembledBar.getRightDisks());
        fillDiskCards(cards, disks);
    }

    private void fillDiskCards(List<Card> cards, List<Disk> disks) {
        Map<BigDecimal, Map<MeasurementUnit, Integer>> disksMap = new HashMap<>();
        for (Disk disk : disks) {
            BigDecimal weight = disk.getWeight();
            Map<MeasurementUnit, Integer> unitMap = disksMap.get(weight);
            if (unitMap == null) {
                unitMap = new HashMap<>();
                disksMap.put(weight, unitMap);
            }
            Integer diskCount = unitMap.get(disk.getUnit());
            diskCount = 1 + (diskCount != null ? diskCount : 0);
            unitMap.put(disk.getUnit(), diskCount);
        }
        Map<BigDecimal, DiskCard> cardsMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<BigDecimal, Map<MeasurementUnit, Integer>> disk : disksMap.entrySet()) {
            for (Map.Entry<MeasurementUnit, Integer> unitMap : disk.getValue().entrySet()) {
                cardsMap.put(disk.getKey().multiply(unitMap.getKey().getCoeff()),
                        new DiskCard(disk.getKey(), unitMap.getValue(), unitMap.getKey(), isShowDiskUnit));
            }
        }
        cards.addAll(cardsMap.values());
    }
}

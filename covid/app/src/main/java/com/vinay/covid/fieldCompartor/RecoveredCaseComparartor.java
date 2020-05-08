package com.vinay.covid.fieldCompartor;

import com.vinay.covid.Utils;
import com.vinay.covid.model.CovidEntity;

import java.util.Comparator;

public class RecoveredCaseComparartor implements Comparator<CovidEntity> {
    private int aesDes;

    public RecoveredCaseComparartor(int aesDes) {
        this.aesDes = aesDes;
    }

    @Override
    public int compare(CovidEntity covidEntity, CovidEntity t1) {
        if (aesDes == Utils.DES) {
            return t1.getRecovered() - covidEntity.getRecovered();
        } else {
            return covidEntity.getRecovered() - t1.getRecovered();
        }
    }
}

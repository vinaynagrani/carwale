package com.vinay.covid.fieldCompartor;

import com.vinay.covid.Utils;
import com.vinay.covid.model.CovidEntity;

import java.util.Comparator;

public class DeathCaseComparator implements Comparator<CovidEntity> {
    private int aesDes;

    public DeathCaseComparator(int aesDes) {
        this.aesDes = aesDes;
    }

    @Override
    public int compare(CovidEntity covidEntity, CovidEntity t1) {
        if (aesDes == Utils.DES) {
            return t1.getDeaths() - covidEntity.getDeaths();
        } else {
            return covidEntity.getDeaths() - t1.getDeaths();
        }
    }
}

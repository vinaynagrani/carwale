package com.vinay.covid.fieldCompartor;

import com.vinay.covid.Utils;
import com.vinay.covid.model.CovidEntity;

import java.util.Comparator;

public class TotalCaseComparator implements Comparator<CovidEntity> {
    private int aesDes;

    public TotalCaseComparator(int aesDes) {
        this.aesDes = aesDes;
    }

    @Override
    public int compare(CovidEntity covidEntity, CovidEntity t1) {
        if (aesDes == Utils.DES) {
            return t1.getConfirmed() - covidEntity.getConfirmed();
        } else {
            return covidEntity.getConfirmed() - t1.getConfirmed();
        }
    }


}

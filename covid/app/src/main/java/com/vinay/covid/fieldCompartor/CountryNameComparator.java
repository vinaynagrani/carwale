package com.vinay.covid.fieldCompartor;

import com.vinay.covid.Utils;
import com.vinay.covid.model.CovidEntity;

import java.util.Comparator;

public class CountryNameComparator implements Comparator<CovidEntity> {
    private int aesDes;

    public CountryNameComparator(int aesDes) {
        this.aesDes = aesDes;
    }

    @Override
    public int compare(CovidEntity covidEntity, CovidEntity t1) {
        if (aesDes == Utils.DES) {
            return t1.getCountry().compareTo(covidEntity.getCountry());
        } else {
            return covidEntity.getCountry().compareTo(t1.getCountry());
        }
    }
}

package com.efundzz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Month {

    JANUARY("Jan", 1,"January"),
    FEBRUARY("Feb", 2,"February"),
    MARCH("Mar", 3,"March"),
    APRIL("Apr", 4,"April"),
    MAY("May", 5,"May"),
    JUNE("June", 6,"June"),
    JULY("July", 7,"July"),
    AUGUST("Aug", 8,"August"),
    SEPTEMBER("Sept", 9,"September"),
    OCTOBER("Oct", 10,"October"),
    NOVEMBER("Nov", 11,"November"),
    DECEMBER("Dec", 12,"December");


    private String shortName;
    private int monthNumber;
    private String monthName;

    public static Month getMonthByNumber(int monthNumber) {
        return Stream.of(Month.values()).filter(month ->
                month.monthNumber == (monthNumber <= 0 ? (12 + (monthNumber % 12)) : monthNumber)).findFirst().orElse(null);
    }
}

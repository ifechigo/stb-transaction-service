package com.suntrustbank.transactions.core.utils;


public class NariaToKobo {
    private final static int KOBO = 100;

    public static int convert(int naria) {
        return Math.abs(naria * KOBO);
    }
}

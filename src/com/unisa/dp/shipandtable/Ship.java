package com.unisa.dp.shipandtable;

public interface Ship {
    /**
     * Define interface of ship
     */
    int getDim();

    int getXStart();

    int getXEnd();

    int getYStart();

    int getYEnd();

    String getName();

    String getType();
}

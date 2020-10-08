package com.unisa.dp.shipandtable;

public class Destroyer implements Ship {
    private final int dim = 3;
    private Integer x_start = null;
    private Integer x_end = null;
    private Integer y_start = null;
    private Integer y_end = null;
    private String name;

    public Destroyer(Integer x_start, Integer x_end, Integer y_start, Integer y_end) {
        this.x_start = x_start;
        this.x_end = x_end;
        this.y_start = y_start;
        this.y_end = y_end;
        this.name = "Destroyer";
    }

    @Override
    public int getDim() {
        return dim;
    }

    @Override
    public int getXStart() {
        return x_start;
    }

    @Override
    public int getXEnd() {
        return x_end;
    }

    @Override
    public int getYStart() {
        return y_start;
    }

    @Override
    public int getYEnd() {
        return y_end;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return this.name.substring(0, 1);
    }
}

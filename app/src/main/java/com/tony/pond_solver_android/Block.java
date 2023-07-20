package com.tony.pond_solver_android;

public class Block {
    public int index;
    public String color;
    public String orientation;
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public Block(int index, String color, String orientation, int x1, int y1, int x2, int y2) {
        this.index = index;
        this.color = color;
        this.orientation = orientation;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void print_block() {
        System.out.printf("Index: %d, Color: %s, Orientation: %s, x1: %d, y1: %d," +
                " x2: %d, y2: %d\n", index, color, orientation, x1, y1, x2, y2);
    }
}

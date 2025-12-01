package org.yourcompany.yourproject.model;

public enum Terrain {
    GRASS(1), MUD(5), WATER(10);
    public final int cost;
    Terrain(int cost) { this.cost = cost; }
}

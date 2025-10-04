package com.example.ygo.model;

public class Card {
    private final String id;
    private final String name;
    private final int atk;
    private final int def;
    private final String imageUrl;

    public Card(String id, String name, int atk, int def, String imageUrl) {
        this.id = id;
        this.name = name;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public String getImageUrl() { return imageUrl; }

    @Override
    public String toString() {
        return String.format("%s (ATK: %d, DEF: %d)", name, atk, def);
    }
}
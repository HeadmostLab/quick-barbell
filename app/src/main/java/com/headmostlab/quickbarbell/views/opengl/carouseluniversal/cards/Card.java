package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards;

public class Card {

    private int id;
    private int textureId;

    public Card() {
    }

    public Card(int textureId) {
        this.textureId = textureId;
    }

    public Card(int id, int textureId) {
        this.id = id;
        this.textureId = textureId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
}
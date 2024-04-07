package com.example.dotacatalog.data;

import java.util.List;

public class Product {
    private String name;
    private String hero;
    private String cost;
    private List<String> images;

    public Product(String name, String hero, String cost, List<String> images) {
        this.name = name;
        this.hero = hero;
        this.cost = cost;
        this.images = images;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}

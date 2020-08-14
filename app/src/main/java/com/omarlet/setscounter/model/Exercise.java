package com.omarlet.setscounter.model;

public class Exercise {
    private String name;
    private int sets;
    private int weight;

    public Exercise(String name, int sets, int weight){
        this.name = name;
        this.sets = sets;
        this.weight = weight;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

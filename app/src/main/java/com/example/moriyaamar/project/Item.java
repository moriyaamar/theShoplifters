package com.example.moriyaamar.project;

public class Item {
    private String itemName;
    private int amount;

    public Item(){

    }


    public Item(String name, int amnt){
        itemName = name;
        amount = amnt;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}

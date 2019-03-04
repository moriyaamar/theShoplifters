package com.example.moriyaamar.project;

import java.util.Objects;

public class Item {
    private String itemName;
    private int amount;

    public Item(){

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return amount == item.getAmount() &&
                Objects.equals(itemName, item.getItemName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(itemName, amount);
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

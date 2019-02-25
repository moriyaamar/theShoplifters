package com.example.moriyaamar.project;

import java.util.ArrayList;

public class ShopList {
    private String listName;
    private ArrayList<Item> shoppingList;

    /**Constructor for new shopping list*/
    public ShopList(String listName) {
        this.listName = listName;
        this.shoppingList = new ArrayList<Item>();
    }

    /**List name getter*/
    public String getListName() {
        return listName;
    }

    /**List name setter*/
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**List getter*/
    public ArrayList<Item> getShoppingList() {
        return shoppingList;
    }

    /**List item setter*/
    public void setItemInShoppingList(Item item) {
        this.shoppingList.add(item);
    }
}

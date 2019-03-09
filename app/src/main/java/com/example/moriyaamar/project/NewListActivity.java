package com.example.moriyaamar.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewListActivity extends AppCompatActivity implements FragAddNewItemDialog.NewItemListener {
    private ShopList currentList;
    private DatabaseReference appDatabase;
    private String uid;
    private static final String LISTS = "lists";
    private ArrayList<Item> basket;
    private ItemAdapter itemAdapter;
    private int currentPosition=-1;
    private int currentMenu=0;              //0 - add item menu , 1 - trash/edit menu
    private boolean longClickIndicator=false;
    private boolean itemExists=false;
    private ArrayList<Integer> marked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        appDatabase = FirebaseDatabase.getInstance().getReference();
        Intent mainIntent = getIntent();                                        //get all data from previous activity
        final String listName = mainIntent.getExtras().getString("LIST_NAME");
        uid = mainIntent.getExtras().getString("UID");

        int state=1;                    //1 - new empty list, 2 - exist list, 3 - list from BroadcastReceiver
        state = mainIntent.getExtras().getInt("STATE");     //check if new or old list

        if(state==1) {                  //create new empty list
            currentList = new ShopList(listName);
            basket = currentList.getShoppingList();
        }
        else if(state==2){              //edit exist list
            currentList = new ShopList(listName);                   //create new shopping list
            basket = currentList.getShoppingList();                 //get the list you chose to edit
            appDatabase.child(LISTS).child(uid).child(listName).addListenerForSingleValueEvent(new ValueEventListener() {       //listener to read all the list from database
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {     //each time list in database is being updated, read the new items and update the basket
                        String itemName = uniqueKeySnapshot.getKey();
                        int itemAmount = Integer.parseInt(uniqueKeySnapshot.getValue().toString());
                        Item i = new Item(itemName, itemAmount);
                        basket.add(i);                                                     //update current basket
                        itemAdapter.notifyDataSetChanged();                                 //update the adapter and show new item in list
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else if(state==3){                          //create new list from broadcast receiver
            ArrayList<Item> shoppingList = (ArrayList<Item>) mainIntent.getExtras().getSerializable("LIST");        //get the list from the intent that was started by the Broadcast receiver

            appDatabase.child(LISTS).child(uid).child(listName).push().setValue("-1");                                  //create new list in database (-1 for magic cookie)
            for(Item i:shoppingList){
                appDatabase.child(LISTS).child(uid).child(listName).child(i.getItemName()).setValue(i.getAmount());     //inset each item in the list into the database
            }

            basket = shoppingList;                                                       //finally, set the current basket as the shoppingList
        }

        ListView list = findViewById(R.id.basketListView);                               //get the GUI's listview
        itemAdapter = new ItemAdapter(getApplicationContext(), R.layout.row_item, basket, uid);     //set the adapter with Context, each row's GUI, the dataset (basket), and the userID to handle the database later
        itemAdapter.setNotifyOnChange(true);                                //set Notify Changes flag to true - each time there is a change in the dataset (basket) update the GUI
        list.setAdapter(itemAdapter);                                       //set the listview's adapter to itemAdapter we just created

        if(state==1) {                                                      //in case of a new list
            appDatabase.child(LISTS).child(uid).child(listName).push().setValue("-1");      //start new list in the data base using magic cookie
        }

        appDatabase.child(LISTS).child(uid).child(listName).addValueEventListener(new ValueEventListener() {        //update the new shopping list every time there are changes
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapshot: dataSnapshot.getChildren()){
                    String itemName = uniqueKeySnapshot.getKey();
                    int itemAmount = Integer.parseInt(uniqueKeySnapshot.getValue().toString());
                    Item i = new Item(itemName, itemAmount);

                    if(itemAmount!=-1) {
                        if(!basket.contains(i)) {
                            basket.add(i);
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {               //every time there is a long click on item, go here
                currentPosition=position;                                              //get the clicked item position
                longClickIndicator=!longClickIndicator;                                 //flip the indicator, when long click happens

                if(longClickIndicator) {                                                //first long click was performed in order to see options
                    view.setSelected(true);                                             //mark the current row that was pressed
                    currentMenu = 1;                                                    //update the menu
                    invalidateOptionsMenu();                                            //
                }
                else {                                                                  //second long click was performed, hide options
                    view.setSelected(false);                                            //unmark current row that was selected
                    currentMenu = 0;                                                    //update the menu
                    invalidateOptionsMenu();                                            //
                }
                return true;
            }
        });
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {                            //prepare the current menu to be performed, 0 - second or no long click wasnt made, 1- first long click was made

        MenuItem plusItem = menu.findItem(R.id.plusMenuItem);                   //get all the MenuItems
        MenuItem trashItem = menu.findItem(R.id.trashMenuItem);                 //
        MenuItem shareItem = menu.findItem(R.id.shareMenuItem);                 //
        MenuItem homeItem = menu.findItem(R.id.homeMenuItem);                   //
        MenuItem alarmItem = menu.findItem(R.id.alarmMenuItem);                 //
        MenuItem editItem = menu.findItem(R.id.editMenuItem);                   //

        if(currentMenu==0) {                                                    //no long click or second long click was made, basic menu
            plusItem.setEnabled(true).setVisible(true);                         //what to show and what to hide
            trashItem.setEnabled(false).setVisible(false);                      //
            shareItem.setEnabled(false).setVisible(false);                      //
            homeItem.setEnabled(true).setVisible(true);                         //
            alarmItem.setEnabled(false).setVisible(false);                      //
            editItem.setEnabled(false).setVisible(false);                       //
        }
        else{                                                                   //long click was made, show advanced menu
            plusItem.setEnabled(false).setVisible(false);                       //
            trashItem.setEnabled(true).setVisible(true);                        //
            shareItem.setEnabled(false).setVisible(false);                      //
            homeItem.setEnabled(true).setVisible(true);                         //
            alarmItem.setEnabled(false).setVisible(false);                      //
            editItem.setEnabled(true).setVisible(true);                         //
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                             //inflate the menu you have created

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                       //option item was selected, check which one
        switch(item.getItemId()){
            case R.id.plusMenuItem:                                             //Create new item
                new FragAddNewItemDialog().show(getSupportFragmentManager(),null);          //Show new item dialog
                return true;
            case R.id.trashMenuItem:
                String itemname = basket.get(currentPosition).getItemName();                     //delete item
                basket.remove(currentPosition);
                itemAdapter.notifyDataSetChanged();
                appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(itemname).removeValue();     //remove item from database
                currentMenu=0;                                                                                          //set the menu to basic mode
                invalidateOptionsMenu();                                                                                //update the menu
                Toast.makeText(getApplicationContext(), "Item was deleted successfully", Toast.LENGTH_SHORT).show();    //show deletion Toast
               return true;
            case R.id.homeMenuItem:                                                                                     //return to the first screen
                Intent retIntent = new Intent(NewListActivity.this, Main2Activity.class);                 //which activity to load
                retIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);                                                     //exit all other activities
                startActivity(retIntent);                                                                               //start new activity
                return true;
            case R.id.editMenuItem:                                                                                     //edit item
                FragAddNewItemDialog frag = new FragAddNewItemDialog();
                Bundle args = new Bundle();                                                                             //new bundle to save the item data for the new fragment
                args.putString("NAME", basket.get(currentPosition).getItemName());                                      //saving the data about the item in the bundle
                args.putInt("AMOUNT", basket.get(currentPosition).getAmount());                                         //
                frag.setArguments(args);                                                                                //saving the bundle in the new fragment
                frag.show(getSupportFragmentManager(),null);                                                        //showing the edit item dialog fragment
                //inflate edit item dialog
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewItemApproved(final String itemName, int itemAmount, boolean edit) {                        //go here when item was approved in the dialog fragment (edit is a bit if it was edited, edit=true ->edited, edit=false->new item)
        Item newItem = new Item();                                                          //create new Item
        int updateIndex=-1;                                                                 //which item to update (-1 as magic cookie)
        newItem.setItemName(itemName);                                                      //set item name
        newItem.setAmount(itemAmount);                                                      //set item amount

        for(int i=0; i<currentList.getShoppingList().size();i++){                           //iterate over the current shopping list and see the item already exists
            if(itemName.equals(currentList.getShoppingList().get(i).getItemName())) {       //check if the item name was found in the list
                itemExists = true;                                                          //if is, itemExists=true
                updateIndex = i;                                                            //update it's index in updateIndex
                }
            }
        if(!itemExists){                                                                    //if item wasnt in the list, create new item
            appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(newItem.getItemName()).setValue(newItem.getAmount());        //insert new item to database
            currentList.setItemInShoppingList(newItem);                                     //update the current shopping list
        }
        else {                                                                              //if item was in the list, check if we wanted to edit it or if we try to add two items with the same name
            if(edit){                                                                       //if we want to edit, OK, edit the item
                currentList.getShoppingList().get(updateIndex).setItemName(itemName);       //
                currentList.getShoppingList().get(updateIndex).setAmount(itemAmount);       //
                basket = currentList.getShoppingList();                                     //update the dataset for the GUI
                itemAdapter.notifyDataSetChanged();                                         //notify to update GUI
                itemExists=false;                                                           //get ready for new items
                //update changes in database
                appDatabase.child(LISTS).child(uid).child(currentList.getListName()).child(newItem.getItemName()).setValue(newItem.getAmount());        //insert item to database
            }
            else {
                Toast.makeText(getApplicationContext(), "Item already in your shopping list", Toast.LENGTH_LONG).show();        //want to add new item but item already exits, show Toast
                itemExists = false;                                                         //get ready for new items
            }
        }

        currentMenu=0;                                                                      //set menu to basic mode
        invalidateOptionsMenu();                                                            //update menu

    }


    public void removeItemFromList(){                                                       //currently not working
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete Item").setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basket.remove(currentPosition);
                        appDatabase.child(LISTS).child(uid).child(currentList.getListName()).removeValue();
                        currentMenu=0;
                        invalidateOptionsMenu();
                        Toast.makeText(getApplicationContext(), "Item was deleted successfully", Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });        //ask if you sure you want to delete item - currently not working****

        builder.create();
    }
}

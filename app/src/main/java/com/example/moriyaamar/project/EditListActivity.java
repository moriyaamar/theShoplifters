package com.example.moriyaamar.project;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditListActivity extends AppCompatActivity {
    private DatabaseReference appDatabase;
    private ListAdapter listAdapter;
    private ArrayList<String> listOfLists;
    private String uid;
    private final String LISTS = "lists";
    private int currentMenu = 0, currentPosition;            //0 - show nothing, 1 - show share option
    private boolean longClickIndicator=false;
    private ShopList shopList;
    private final int state = 2;
 //   private Bundle listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Intent mainItent = getIntent();
        uid = mainItent.getStringExtra("UID");

        listOfLists = new ArrayList<String>();

        ListView list = findViewById(R.id.listOfListsListView);

        listAdapter = new ListAdapter(getApplicationContext(), R.layout.list_of_lists_row_item, listOfLists);
        listAdapter.setNotifyOnChange(true);
        list.setAdapter(listAdapter);

        appDatabase = FirebaseDatabase.getInstance().getReference();

        //go through current user id and get all of his lists
        appDatabase.child(LISTS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                    listOfLists.add(uniqueKeySnapshot.getKey().toString());
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;
                longClickIndicator=!longClickIndicator;
                String listName = ((TextView)view.findViewById(R.id.listNameTextView)).getText().toString();
                shopList = new ShopList(listName);

                if(longClickIndicator) {
                    view.setSelected(true);
                    currentMenu = 1;
                    invalidateOptionsMenu();
                }
                else {
                    view.setSelected(false);
                    currentMenu = 0;
                    invalidateOptionsMenu();
                }
                return true;
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String listName = ((TextView)(view.findViewById(R.id.listNameTextView))).getText().toString();
                Intent argsIntent = new Intent(EditListActivity.this, NewListActivity.class);
                argsIntent.putExtra("STATE", state);
                argsIntent.putExtra("UID", uid);
                argsIntent.putExtra("LIST_NAME",listName);
                EditListActivity.this.startActivity(argsIntent);
            }

        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {            //Arrange menu

        MenuItem plusItem = menu.findItem(R.id.plusMenuItem);
        MenuItem trashItem = menu.findItem(R.id.trashMenuItem);
        MenuItem shareItem = menu.findItem(R.id.shareMenuItem);
        MenuItem homeItem = menu.findItem(R.id.homeMenuItem);
        MenuItem alarmItem = menu.findItem(R.id.alarmMenuItem);
        MenuItem editItem = menu.findItem(R.id.editMenuItem);

        if(currentMenu==0) {
            plusItem.setEnabled(false).setVisible(false);
            trashItem.setEnabled(false).setVisible(false);
            shareItem.setEnabled(false).setVisible(false);
            homeItem.setEnabled(true).setVisible(true);
            alarmItem.setEnabled(false).setVisible(false);
            editItem.setEnabled(false).setVisible(false);
        }
        else{
            plusItem.setEnabled(false).setVisible(false);
            trashItem.setEnabled(true).setVisible(true);
            shareItem.setEnabled(true).setVisible(true);
            homeItem.setEnabled(true).setVisible(true);
            alarmItem.setEnabled(false).setVisible(false);
            editItem.setEnabled(false).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.shareMenuItem:
                //start a sharing option
                sendShopListViaMessage(shopList.getListName());
                return true;
            case R.id.homeMenuItem:
                finish();
                return true;
            case R.id.trashMenuItem:                //trash the entire list
                removeEntireShoppingList(shopList.getListName());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void removeEntireShoppingList(String listName) {

        listOfLists.remove(listName);
        listAdapter.notifyDataSetChanged();

        appDatabase.child(LISTS).child(uid).child(listName).removeValue();
        //update database after remove
    }

    private void sendShopListViaMessage(String listName) {
        appDatabase.child(LISTS).child(uid).child(listName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    String itemName = uniqueKeySnapshot.getKey();
                    int itemAmount = Integer.parseInt(uniqueKeySnapshot.getValue().toString());
                    Item i = new Item(itemName, itemAmount);
                    shopList.setItemInShoppingList(i);
                }

                String textList = getListAsText(shopList);
                if(!textList.equals("")){
                    Intent messasgeIntent = new Intent(Intent.ACTION_SEND);
                    messasgeIntent.setType("text/plain");
                    messasgeIntent.setPackage("com.whatsapp");
                    messasgeIntent.putExtra(Intent.EXTRA_TEXT, textList);
                    messasgeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try{
                        getApplicationContext().startActivity(messasgeIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Whatsapp hav not been installed", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public String getListAsText(ShopList list){
        String listText=list.getListName()+"\n";
        ArrayList<Item> items = list.getShoppingList();
        for(Item i: items){
            listText+="\t-"+i.getItemName()+": "+i.getAmount()+"\n";
        }
        return listText;
    }

}

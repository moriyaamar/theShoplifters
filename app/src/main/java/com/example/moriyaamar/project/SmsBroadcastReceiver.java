package com.example.moriyaamar.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;


public class SmsBroadcastReceiver extends BroadcastReceiver {
    private String userId;

    public SmsBroadcastReceiver(String uid) {
        this.userId = uid;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String smsMessageBody=null;
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] message = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for(int i=0;i<message.length;i++) {
                    String smsSource = message[i].getOriginatingAddress();
                    smsMessageBody = message[i].getMessageBody();
                }
            }

            if (smsMessageBody!=null) {
                ShopList list = parseMessageToList(smsMessageBody);

                if(list!=null){
                    //dispatch list for creation *\.*'~'*./*
                    Intent myIntent = new Intent(context, NewListActivity.class);
                    myIntent.putExtra("LIST_NAME", list.getListName());       //pass ListName
                    myIntent.putExtra("UID", userId);
                    myIntent.putExtra("STATE",3);
                    myIntent.putExtra("LIST",list.getShoppingList());
                    context.startActivity(myIntent);
                }
            }

            ///////////////////////////////////////////////////////////////////////////////
            //////////////////////PARSE MESSAGE AND BUILD NEW LIST/////////////////////////
            ///////////////////////////////////////////////////////////////////////////////

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ShopList parseMessageToList(String message) {
        ShopList retList;
        String itemName;
        int itemAmount=0;
        String[] lines = message.split("\\r?\\n");
        if (lines[0].substring(0, 4).equals("List")) {         //Check if it is really a shopping list, if yes, create one
            retList = new ShopList((lines[0]));                  //new shopping list name
            for (int i = 1; i < lines.length; i++) {                       //iterate over all of the groceries
                itemName = lines[i].substring(1, lines[i].indexOf(":"));
                try {
                    String amount = lines[i].substring(lines[i].indexOf(":")+1, lines[i].length());
                    itemAmount = Integer.parseInt(amount);
                }catch (Exception e){
                    System.out.println("Inconvertible type");
                }
                Item item = new Item(itemName, itemAmount);
                retList.setItemInShoppingList(item);
            }
            return retList;
        }
        return null;
    }
}

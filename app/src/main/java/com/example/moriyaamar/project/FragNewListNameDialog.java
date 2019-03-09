package com.example.moriyaamar.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class FragNewListNameDialog extends DialogFragment {

    private ListNameListener listNameListener;
    private EditText listNameEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragNewListNameDialog.ListNameListener)
            listNameListener=(FragNewListNameDialog.ListNameListener)context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
       // return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("List Name").setMessage("Set new list name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!(listNameEditText.getText().toString()).equals("")) {
                            listNameListener.onListNameApproved(listNameEditText.getText().toString());
                        }
                        else {
                            Date currentDate = Calendar.getInstance().getTime();
                            listNameListener.onListNameApproved(currentDate.toString().substring(0,19));
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        View view=getActivity().getLayoutInflater().inflate(R.layout.frag_insert_list_name_dialog,null);
        builder.setView(view);

        listNameEditText = (EditText)view.findViewById(R.id.nameEditText);

        return builder.create();
    }

    public interface ListNameListener{
        public void onListNameApproved(String name);
    }


}

package com.example.moriyaamar.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragAddAlarmDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NewAlarmListener newAlarmListener;
    private TextView dateTextView;
    private Calendar calendar;

    private String currentDateTime;
    private int year, day, month, hour, minute;

    public FragAddAlarmDialog() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragAddAlarmDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FragAddAlarmDialog newInstance(String param1, String param2) {
        FragAddAlarmDialog fragment = new FragAddAlarmDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (newAlarmListener != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragAddAlarmDialog.NewAlarmListener) {
            newAlarmListener= (FragAddAlarmDialog.NewAlarmListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        newAlarmListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        calendar = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("New Item").setMessage("Add new item to your list")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dateTextView.getText().toString().equals("Date")){
                            Toast.makeText(getContext(), "Alarm cancelled - no date was selected",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                        else {
                            long res = calendar.getTimeInMillis();
                            newAlarmListener.onNewAlarmApproved(calendar.getTimeInMillis());
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        View view=getActivity().getLayoutInflater().inflate(R.layout.frag_add_alarm_dialog,null);
        builder.setView(view);

        dateTextView = (TextView)view.findViewById(R.id.dateTextView);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int paramYear, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, paramYear);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        year = paramYear;
                        month = monthOfYear;
                        day = dayOfMonth;

                        calendar.set(year, month, day, hour, minute);

                        updateLabel();
                    }

                    private void updateLabel(){
                        currentDateTime = dateTextView.getText().toString();
                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                        currentDateTime = sdf.format(myCalendar.getTime())+"    "+ currentDateTime;
                        dateTextView.setText(currentDateTime);
                    }

                };

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                final String currentDateandTime = sdf.format(new Date());
                year = Integer.parseInt(currentDateandTime.substring(0,4));
                month = Integer.parseInt(currentDateandTime.substring(4,6));
                day = Integer.parseInt(currentDateandTime.substring(6,8));
                DatePickerDialog dpd = new DatePickerDialog( getContext(), date, year,month,day);
                dpd.show();

                //SHOW TIME PICKER
              //  int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
               // final int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        hour = selectedHour;
                        minute = selectedMinute;


                        //calendar.set(Calendar.HOUR, selectedHour);
                        //calendar.set(Calendar.MINUTE,selectedMinute);
                        updateLabel(selectedHour, selectedMinute);
                    }

                    private void updateLabel(int selectedHour, int selectedMinute){
                        currentDateTime=Integer.toString(selectedHour)+":"+Integer.toString(selectedMinute);
                        dateTextView.setText(currentDateTime);
                    }

                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        return builder.create();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NewAlarmListener {
        // TODO: Update argument type and name
        void onNewAlarmApproved(long alarmDateTime);
    }
}

package com.example.moran.tictaccook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * Created by moran on 31/07/2017.
 */

interface MyOnTimeSetListener{
    void onTimeSet(int hour,int minutes); //the parameters are according what the modol need to return back
}

public class MyTimePicker extends EditText implements MyOnTimeSetListener{

    public MyTimePicker(Context context) {
        super(context);
    }

    public MyTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Log.d("TAG","event.getAction() == MotionEvent.ACTION_DOWN");
            MyTimePickerDialog tpd = MyTimePickerDialog.newInstance(getId());
            tpd.show(((Activity)getContext()).getFragmentManager(),"TAG");
            return true; //means we handled the event
        }
        return true;
    }

    @Override
    public void onTimeSet(int hour, int minutes) {
        if ((hour != 00 && minutes != 00) || (hour == 00 && minutes == 00)) {
            setText("" + hour + " Hours and " + minutes + " Mins");
        } else if (hour == 00 && minutes != 00) {
            setText("" + minutes + " Mins");
        } else if (hour != 00 && minutes == 00) {
            setText("" + hour + " Hours");
        }
    }

    //inner class represent the DialogFragment
    public static class MyTimePickerDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        private static final String ARG_CONTAINER_EDIT_TEXT_VIEW_ID = "edit_text_containerId";

        MyOnTimeSetListener listener;

        public static MyTimePickerDialog newInstance(int editTextId) {
            MyTimePickerDialog timePickerDialog = new MyTimePickerDialog();
            Bundle args = new Bundle();
            args.putInt(ARG_CONTAINER_EDIT_TEXT_VIEW_ID, editTextId);
            timePickerDialog.setArguments(args);
            return timePickerDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            Dialog timePicker = new TimePickerDialog(getActivity(),this,00,00,true);//this = TimePickerDialog.OnTimeSetListener

            if(getArguments()!=null){
                int editTextId = getArguments().getInt(ARG_CONTAINER_EDIT_TEXT_VIEW_ID);
                listener = (MyOnTimeSetListener) getActivity().findViewById(editTextId);
            }
            return timePicker;
        }

        //override from TimePickerDialog.OnTimeSetListener
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("TAG","onTimeSet " + hourOfDay + " Hour and " + minute + " Mins");
            listener.onTimeSet(hourOfDay,minute);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("TAG", "TimeDialog destroyed");
        }
    }
}

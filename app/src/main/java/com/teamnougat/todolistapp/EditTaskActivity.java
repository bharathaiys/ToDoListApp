package com.teamnougat.todolistapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener, OnGesturePerformedListener{

    private static final String TAG = "EditTaskActivity";
    private TaskDbHelper myHelper;
    private Spinner textType;
    private EditText textTitle, textLocation;
    private TextView textDate, textTime;
    private String task_id, task_name, task_type, task_date, task_time, task_loc, createyear, createmonth, createdate, createhour, createmin;

    private int tYear, tMonth, tDate, tDay, tHour, tMinute, newMonth;
    private String finalDate, sMonth, sDate, sHour, sMinute;
    private GestureLibrary gestLib;
    private final Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        task_id = extras.getString("TASK_ID");
        task_name = extras.getString("TASK_NAME");
        task_type = extras.getString("TASK_TYPE");
        task_date = extras.getString("TASK_DATE");
        task_time = extras.getString("TASK_TIME");
        task_loc = extras.getString("TASK_LOC");
        createyear = extras.getString("CYEAR");
        createmonth = extras.getString("CMONTH");
        createdate = extras.getString("CDATE");
        createhour = extras.getString("CHOUR");
        createmin = extras.getString("CMIN");
        Log.d(TAG, task_id);

        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_edit_task, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);
        getViews();
        getTaskDetails();

        textDate.setOnClickListener(this);
        textTime.setOnClickListener(this);
    }

    private void getViews()
    {
        textTitle = (EditText) findViewById(R.id.edit_taskName);
        textType = (Spinner) findViewById(R.id.edit_taskType);
        textDate = (TextView) findViewById(R.id.edit_date);
        textTime = (TextView) findViewById(R.id.edit_time);
        textLocation = (EditText) findViewById(R.id.edit_location);
    }

    private void getTaskDetails() {
         myHelper = new TaskDbHelper(this);
        textTitle.setText(task_name);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        textType.setAdapter(adapter);
        if (!task_type.equals(null)) {
            int spinnerPosition = adapter.getPosition(task_type);
            textType.setSelection(spinnerPosition);
        }

        tYear = Integer.parseInt(createyear);
        tMonth = Integer.parseInt(createmonth)-1;
        newMonth = tMonth; newMonth++;
        tDate = Integer.parseInt(createdate);
        tDay = cal.get(Calendar.DAY_OF_WEEK);
        tHour = Integer.parseInt(createhour);
        tMinute = Integer.parseInt(createmin);
        sHour = ""; sMinute = "";
        sDate = ""; sMonth = "";
        if( tDate < 10 )
            sDate = "0";
        sDate += tDate;
        if( tMonth-1 < 10 )
            sMonth = "0";
        sMonth += newMonth;

        finalDate = tYear + "-" + sMonth + "-" + sDate;

        textDate.setText(task_date);
        textTime.setText(task_time);
        textLocation.setText(task_loc);
    }

    public String findMonth( int mMonth ){
        String sMonth = "";
        if(mMonth == '1')
            sMonth = "Jan";
        else if (mMonth == '2')
            sMonth = "Feb";
        else if (mMonth == '3')
            sMonth = "Mar";
        else if (mMonth == '4')
            sMonth = "Apr";
        else if (mMonth == '5')
            sMonth = "May";
        else if (mMonth == '6')
            sMonth = "Jun";
        else if (mMonth == '7')
            sMonth = "Jul";
        else if (mMonth == '8')
            sMonth = "Aug";
        else if (mMonth == '9')
            sMonth = "Sep";
        else if (mMonth == Integer.parseInt("10"))
            sMonth = "Oct";
        else if (mMonth == Integer.parseInt("11"))
            sMonth = "Nov";
        else
        {
            sMonth = "Dec";
        }
        return sMonth;
    }

    @Override
    public void onClick(View v) {
        if( v == textDate ) {
            DatePickerDialog d = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                            Date date = new Date(year, monthOfYear, dayOfMonth-1);
                            String dayOfWeek = sdf.format(date);
                            tDate = dayOfMonth;
                            tMonth = monthOfYear;
                            newMonth = tMonth; newMonth++;
                            tYear = year;
                            sDate = ""; sMonth = "";
                            if( tDate < 10 )
                                sDate = "0";
                            sDate += tDate;
                            if( tMonth < 10 )
                                sMonth = "0";
                            sMonth += newMonth;
                            textDate.setText( dayOfWeek + ", " + findMonth(tMonth+1) + " " + sDate + ", " + tYear );
                            int setMon = tMonth+1;
                            if( setMon < 10 )
                                sMonth = "0";
                            sMonth += setMon;
                            finalDate = tYear + "-" + sMonth + "-" + sDate;
                        }
                    }, tYear, tMonth, tDate);
            d.show();
        }
        if( v == textTime ) {
            final Calendar cal = Calendar.getInstance();
            TimePickerDialog t = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            tHour = hourOfDay;
                            tMinute = minute;
                            sHour = ""; sMinute = "";
                            if( tHour < 10 )
                                sHour = "0";
                            sHour += "" + tHour;
                            if( tMinute < 10 )
                                sMinute = "0";
                            sMinute += "" + tMinute;
                            textTime.setText(sHour + ":" + sMinute);
                        }
                    }, tHour, tMinute, false);
            t.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                if( textTitle.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    updateDB();
                    Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                    i.putExtra("TASK_ID", task_id);
                    startActivityForResult(i, 1);
                    this.finish();
                }
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                i.putExtra("TASK_ID", task_id);
                startActivityForResult(i, 1);
                this.finish();
            }
        }
    }

    public void updateDB() {

        finalDate = finalDate + " " + textTime.getText().toString() + ":00";
        try
        {
            SQLiteDatabase dbase = myHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(TaskContract.TaskEntry.COL_TASK_TITLE, textTitle.getText().toString().trim());
            cv.put(TaskContract.TaskEntry.COL_TASK_TYPE, textType.getSelectedItem().toString());
            cv.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, finalDate);
            cv.put(TaskContract.TaskEntry.COL_TASK_DUEDAY, textDate.getText().toString().trim().substring(0,3));
            cv.put(TaskContract.TaskEntry.COL_TASK_LOCATION, textLocation.getText().toString().trim());
            cv.put(TaskContract.TaskEntry.COL_TASK_KEY, 1);

            dbase.update(TaskContract.TaskEntry.TABLE, cv, TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id, null);
            dbase.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage().toString());
        }
    }

}

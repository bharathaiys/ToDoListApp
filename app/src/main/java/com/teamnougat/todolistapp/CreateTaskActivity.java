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
import android.view.MenuItem;
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

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener, OnGesturePerformedListener{

    private static final String TAG = "CreateTaskActivity";
    private TaskDbHelper myHelper;
    private Spinner createTaskType, taskType;
    private EditText createtaskName, taskLocation;
    private TextView createdate, createtime;
    private EditText taskName;
    private int tYear, tMonth, tDate, tDay, tHour, tMinute, newMonth;
    private String finalDate, sMonth, sDate, sHour, sMinute;
    private GestureLibrary gestLib;
    private final Calendar cal = Calendar.getInstance();
    private ArrayAdapter<CharSequence> typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_create_task, null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        getViews();
        getVars();

        createdate.setText( findDay(tDay) + ", " + findMonth(tMonth+1) + " " + sDate + ", " + tYear );
        createtime.setText("23" + ":" + "59");
        createdate.setOnClickListener(this);
        createtime.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void getVars()
    {
        myHelper = new TaskDbHelper(this);

        typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_types, R.layout.spinner_item);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        taskType.setAdapter(typeAdapter);

        tYear = cal.get(Calendar.YEAR);
        tMonth = cal.get(Calendar.MONTH);
        newMonth = tMonth;  newMonth++;
        tDate = cal.get(Calendar.DAY_OF_MONTH);
        tDay = cal.get(Calendar.DAY_OF_WEEK);
        tHour = cal.get(Calendar.HOUR_OF_DAY);
        tMinute = cal.get(Calendar.MINUTE);
        sHour = ""; sMinute = "";
        sDate = ""; sMonth = "";
        if( tDate < 10 )
            sDate = "0";
        sDate += tDate;
        if( tMonth < 10 )
            sMonth = "0";
        sMonth += newMonth;

        finalDate = tYear + "-" + sMonth + "-" + sDate;
    }



    @Override   //Insert
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() ==  R.id.action_hand)
        {
            Intent i = new Intent(getApplicationContext(), HandWritingActivity.class);
            Toast.makeText(getApplicationContext(), "Start Writing!",
                    Toast.LENGTH_LONG).show();
            startActivityForResult(i, 1);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }



    public String findDay( int mDay ){
        String sDay = "";
        if(mDay == 1)
            sDay = "Sun";
        else if(mDay == 2)
            sDay = "Mon";
        else if(mDay == 3)
            sDay = "Tue";
        else if(mDay == 4)
            sDay = "Wed";
        else if(mDay == 5)
            sDay = "Thu";
        else if(mDay == 6)
            sDay = "Fri";
        else if(mDay == 7)
            sDay = "Sat";
        else
        {
            sDay = "No" + mDay;
        }

        return sDay;
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

    public void insertDb() {
        SQLiteDatabase dbase = myHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        finalDate = finalDate + " " + createtime.getText().toString() + ":00";

        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, createtaskName.getText().toString().trim());
        values.put(TaskContract.TaskEntry.COL_TASK_TYPE, createTaskType.getSelectedItem().toString());
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDATE, finalDate);
        values.put(TaskContract.TaskEntry.COL_TASK_DUEDAY, createdate.getText().toString().substring(0,3));
        if(!taskLocation.getText().toString().isEmpty())
            values.put(TaskContract.TaskEntry.COL_TASK_LOCATION, taskLocation.getText().toString().trim());

        dbase.insert(TaskContract.TaskEntry.TABLE, null, values);
        Log.d(TAG, "Inserted");
        dbase.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_CANCELED )
        {
            String newS = data.getStringExtra("Task_Name");
            if( !newS.isEmpty() )
                taskName.setText(newS);
        }
        else
        {

        }
    }
    @Override
    public void onClick(View v) {
        if( v == createdate ) {
            final Calendar c = Calendar.getInstance();

            DatePickerDialog d = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
                            Date date = new Date(year, monthOfYear, dayOfMonth-1);
                            String dayOfWeek = sdf.format(date);
                            tDate = dayOfMonth;
                            tMonth = monthOfYear;   newMonth = tMonth;  newMonth++;
                            tYear = year;
                            sDate = ""; sMonth = "";
                            if( tDate < 10 )
                                sDate = "0";
                            sDate += tDate;
                            if( tMonth < 10 )
                                sMonth = "0";
                            sMonth += newMonth;
                            finalDate = tYear + "-" + sMonth + "-" + sDate;
                            createdate.setText( dayOfWeek + ", " + findMonth(tMonth+1) + " " + sDate + ", " + tYear );
                        }
                    }, tYear, tMonth, tDate);
            d.show();
        }
        if( v == createtime ) {
            final Calendar c = Calendar.getInstance();
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
                            createtime.setText(sHour + ":" + sMinute);
                        }
                    }, tHour, tMinute, false);
            t.show();
        }
    }
    private void getViews()
    {
        createtaskName = (EditText)findViewById(R.id.create_taskName);
        createTaskType = (Spinner) findViewById(R.id.create_taskType);
        createdate = (TextView) findViewById(R.id.create_date);
        createtime = (TextView) findViewById(R.id.create_time);
        taskLocation = (EditText)findViewById(R.id.create_location);
        taskType = (Spinner) findViewById(R.id.create_taskType);
        taskName = (EditText) findViewById(R.id.create_taskName);
    }
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                if( createtaskName.getText().toString().isEmpty() ){
                    Toast.makeText(this, "Task Name Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    insertDb();
                    setResult(RESULT_CANCELED, null);
                    finish();
                }
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }
}
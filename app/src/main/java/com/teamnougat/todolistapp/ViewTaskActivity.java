package com.teamnougat.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.util.ArrayList;

public class ViewTaskActivity extends AppCompatActivity implements OnGesturePerformedListener{

    private static final String TAG = "ViewTaskActivity";
    private TaskDbHelper myHelper;
    private GestureLibrary gestLib;
    String task_id, newDate;
    String smonth, sdate, syear, sHour, sMin;
    ActionBar ab;
    TextView text_Title;
    TextView text_Type;
    TextView text_Date;
    TextView text_Time;
    TextView text_Location;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task_id = getIntent().getStringExtra("TASK_ID");
        Log.d(TAG, task_id);
        GestureOverlayView gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_view_task, null);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);
        myHelper = new TaskDbHelper(this);
        getTaskDetails();
    }
    public void deleteDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TaskContract.TaskEntry.TABLE + " WHERE " +
                TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";
        db.execSQL(updateQuery);
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateDb() {
        SQLiteDatabase db = myHelper.getWritableDatabase();
        String updateQuery = "UPDATE " + TaskContract.TaskEntry.TABLE + " SET " +
                TaskContract.TaskEntry.COL_TASK_KEY + "=0 WHERE " + TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";
        db.execSQL(updateQuery);
        db.close();
    }
    private void getTaskDetails() {
        SQLiteDatabase dbase = myHelper.getReadableDatabase();

        String selectQuery = "SELECT " + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDATE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDAY + ", "
                + TaskContract.TaskEntry.COL_TASK_LOCATION
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_ID + "=" +task_id +";";

        Cursor cursor = dbase.rawQuery(selectQuery, null);

        text_Title = (TextView)findViewById(R.id.txv_TaskName);
        text_Type = (TextView)findViewById(R.id.txv_TaskType);
        text_Date = (TextView)findViewById(R.id.txv_DueDate);
        text_Time = (TextView)findViewById(R.id.txv_DueTime);
        text_Location = (TextView)findViewById(R.id.txv_Location);

        if (cursor.moveToFirst()) {
            text_Title.setText(cursor.getString(0));
            if( cursor.getString(1) != null )
            {
                if( cursor.getString(1).equalsIgnoreCase("personal") )
                {
                    Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_action_home );
                    text_Type.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                }
                else if( cursor.getString(1).equalsIgnoreCase("work") )
                {
                    Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_action_work );
                    text_Type.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                }
                else
                {
                    Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.ic_action_other);
                    text_Type.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
            }
            text_Type.setText(cursor.getString(1));
            String[] input = cursor.getString(2).split(" ");
            text_Time.setText(input[1].substring(0,5));
            String[] tmpTime = input[1].split(":");
            sHour = tmpTime[0];
            sMin = tmpTime[1];
            input = input[0].split("-");
            smonth = input[1];
            sdate = input[2];
            syear = input[0];

            if(input[1].equals("01"))
                newDate = "Jan";
            else if (input[1].equals("02"))
                newDate = "Feb";
            else if (input[1].equals("03"))
                newDate = "Mar";
            else if (input[1].equals("04"))
                newDate = "Apr";
            else if (input[1].equals("05"))
                newDate = "May";
            else if (input[1].equals("06"))
                newDate = "Jun";
            else if (input[1].equals("07"))
                newDate = "Jul";
            else if (input[1].equals("08"))
                newDate = "Aug";
            else if (input[1].equals("09"))
                newDate = "Sep";
            else if (input[1].equals("10"))
                newDate = "Oct";
            else if (input[1].equals("11"))
                newDate = "Nov";
            else {
                newDate = "Dec";
            }
            newDate = cursor.getString(3) + ", " + input[1] + " " + input[2] + ", " + input[0];
            text_Date.setText(newDate);
            if( cursor.getString(4) == null )
            {
                text_Location.setText("No Location");
                text_Location.setTextColor(Color.parseColor("#A9A9A9"));
            }
            else
                text_Location.setText(cursor.getString(4));
        }
        else{
            Toast.makeText(this, "Task Not Found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        dbase.close();
    }





    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {
        ArrayList<Prediction> predictions = gestLib.recognize(gesture);
        for (Prediction prediction : predictions)
        {
            if (prediction.score > 3.0 && prediction.name.toLowerCase().equals("edit"))
            {
                Intent i = new Intent(getApplicationContext(), EditTaskActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TASK_ID", task_id);
                extras.putString("TASK_NAME", text_Title.getText().toString());
                extras.putString("TASK_TYPE", text_Type.getText().toString());
                extras.putString("TASK_DATE", text_Date.getText().toString());
                extras.putString("CYEAR", syear);
                extras.putString("CMONTH", smonth);
                extras.putString("CDATE", sdate);
                extras.putString("TASK_TIME", text_Time.getText().toString());
                extras.putString("CHOUR", sHour);
                extras.putString("CMIN", sMin);
                extras.putString("TASK_LOC", text_Location.getText().toString());
                i.putExtras(extras);
                startActivityForResult(i, 1);
                finish();
            }
            else if (prediction.score > 4.0 && prediction.name.toLowerCase().equals("tick")) {
                updateDb();
                setResult(RESULT_CANCELED, null);
                finish();
            }
            else if (prediction.score > 2.0 && prediction.name.toLowerCase().equals("alpha")) {
                Toast.makeText(this, "Task Deleted!", Toast.LENGTH_SHORT).show();
                deleteDb();
                setResult(RESULT_CANCELED, null);
                finish();
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        }
    }
}

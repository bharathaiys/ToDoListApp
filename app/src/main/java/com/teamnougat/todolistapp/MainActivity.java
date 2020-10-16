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
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {

    private static final String TAG = "MainActivity";
    private TaskDbHelper myHelper;
    private ListView myList;
    private NewArrayAdapter myAdapter;
    private GestureLibrary gestLib;
    private ArrayList<String> task_Id;
    private final Calendar cal = Calendar.getInstance();
    private Date newDate;
    private String sDate;
    private SimpleDateFormat dateFormat;
    private float scaleFactor;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    GestureOverlayView gestureOverLay;
    ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        task_Id = new ArrayList<>();
        myHelper = new TaskDbHelper(this);
        myList = (ListView) findViewById(R.id.list_todo);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.add(Calendar.DAY_OF_YEAR, 0);
        newDate = cal.getTime();
        sDate = dateFormat.format(newDate);

        String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
        updateUI(selectQuery);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int temp = (int)id;
                String msg = "" + task_Id.get(temp);
                Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
                i.putExtra("TASK_ID", msg);
                startActivityForResult(i, 1);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                        + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                        + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
                    updateUI(selectQuery);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
        });

        mScaleDetector =  new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        scaleFactor = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Insert
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_CANCELED || resultCode == RESULT_OK )
        {
            String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                    + " WHERE " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                    + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                    + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";";
            updateUI(selectQuery);
        }
        else if( resultCode == RESULT_FIRST_USER )
        {
            String tempStartDate = data.getStringExtra("TASK_DATE") + " 00:00:00";
            String tempEndDate = data.getStringExtra("TASK_DATE") + " 23:59:59";
            String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                    + " WHERE " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + tempStartDate + "\""
                    + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " <= \"" + tempEndDate + "\""
                    + ";";
            updateUI(selectQuery);
        }
    }

    private void updateUI(String selectQuery) {
        ArrayList<Item> taskList = new ArrayList<>();
        task_Id.clear();
        SQLiteDatabase dbase = myHelper.getReadableDatabase();

        Cursor cursor = dbase.rawQuery(selectQuery, null);
        String newDate = "";

        if (cursor.moveToFirst()) {
            do {
                task_Id.add(cursor.getString(0));
                String[] input = cursor.getString(3).split(" ");
                input = input[0].split("-");

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

                taskList.add(new Item(cursor.getString(1), cursor.getString(2), newDate));
            } while (cursor.moveToNext());
        }

        if (myAdapter == null) {
            myAdapter = new NewArrayAdapter(this,
                    R.layout.item_todo,
                    taskList);
            myList.setAdapter(myAdapter);
        } else {
            myAdapter.clear();
            myAdapter.addAll(taskList);
            myAdapter.notifyDataSetChanged();
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
            if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("p")) {
                String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                        + " WHERE " + TaskContract.TaskEntry.COL_TASK_TYPE + " = \"Personal\""
                        + " AND " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                        + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";"
                        + ";";
                updateUI(selectQuery);
            }
            else if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("w")) {
                String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                        + " WHERE " + TaskContract.TaskEntry.COL_TASK_TYPE + " = \"Work\""
                        + " AND " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                        + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";"
                        + ";";
                updateUI(selectQuery);
            }
            else if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("o")) {
                String selectQuery = "SELECT * FROM " + TaskContract.TaskEntry.TABLE
                        + " WHERE " + TaskContract.TaskEntry.COL_TASK_TYPE + " = \"Other\""
                        + " AND " + TaskContract.TaskEntry.COL_TASK_KEY + "=1"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " >= \"" + sDate + "\""
                        + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE + ";"
                        + ";";
                updateUI(selectQuery);
            }
            else if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("c")) {
                Intent i = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(i, 1);
            }
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            scaleFactor *= detector.getScaleFactor();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            scaleFactor = detector.getScaleFactor();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if( scaleFactor > 1 )
            {
                return;
            }
            else if( scaleFactor < 1 )
            {
                Intent i = new Intent(getApplicationContext(), WeeklyViewActivity.class);
                startActivityForResult(i, 1);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        mScaleDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}

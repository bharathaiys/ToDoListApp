package com.teamnougat.todolistapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnougat.todolistapp.db.TaskContract;
import com.teamnougat.todolistapp.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyViewActivity extends AppCompatActivity implements View.OnClickListener, GestureOverlayView.OnGesturePerformedListener {

    private static final String TAG = "WeeklyViewActivity";
    private TaskDbHelper myHelper;
    private int mYear, mMonth, mDay, subd, row_cnt;
    private float scaleFactor;
    private String sDate, eDate, sMonth;
    private Date newDate;
    private SimpleDateFormat dateFormat;
    private float density;
    private TextView[] myDay = new TextView[7];
    private String[] dateString = new String[7];
    private View[] myMarker = new View[7];
    private LinearLayout[] myLayout = new LinearLayout[7];
    private LinearLayout[] myDateLayout = new LinearLayout[7];
    private TextView myMonth;
    private TextView[] message;
    private ArrayList<String> task_Id;
    private final Calendar cal = Calendar.getInstance();
    private GestureLibrary gestLib;

    GestureOverlayView gestureOverLay;
    ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gestureOverLay = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_weekly_view, null);
        gestureOverLay.addView(inflate);
        gestureOverLay.addOnGesturePerformedListener(this);
        gestLib = GestureLibraries.fromRawResource(this, R.raw.gesture);
        gestureOverLay.setGestureColor(getResources().getColor(R.color.gesture_color));
        if(!gestLib.load())
            finish();
        setContentView(gestureOverLay);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getviews();
        getVars();

        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_WEEK);
        sMonth = getMonth(mMonth);

        myMonth.setText( sMonth + " " + mYear );
        myMarker[mDay-1].setVisibility(View.VISIBLE);

        subd = 1 - mDay;
        cal.add(Calendar.DAY_OF_YEAR, subd);
        newDate = cal.getTime();
        sDate = dateFormat.format(newDate);
        dateString[0] = sDate;
        myDay[0].setText( sDate.substring(8) );
        myDateLayout[0].setOnClickListener(this);

        for( int i = 1; i < 7; i++ )
        {
            myDateLayout[i].setOnClickListener(this);

            cal.add(Calendar.DAY_OF_YEAR, 1);
            newDate = cal.getTime();
            eDate = dateFormat.format(newDate);
            dateString[i] = eDate;
            myDay[i].setText( eDate.substring(8) );
        }
        getTaskDetails();

        mScaleDetector =  new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
    }

    private void getviews()
    {
        myMonth = (TextView)findViewById(R.id.currentMonthTextView);
        myDay[0] = (TextView)findViewById(R.id.sundayDateTextView);
        myDay[1] = (TextView)findViewById(R.id.mondayDateTextView);
        myDay[2] = (TextView)findViewById(R.id.tuesdayDateTextView);
        myDay[3] = (TextView)findViewById(R.id.wednesdayDateTextView);
        myDay[4] = (TextView)findViewById(R.id.thursdayDateTextView);
        myDay[5] = (TextView)findViewById(R.id.fridayDateTextView);
        myDay[6] = (TextView)findViewById(R.id.saturdayDateTextView);
        myMarker[0] = findViewById(R.id.sundayMarkerView);
        myMarker[1] = findViewById(R.id.mondayMarkerView);
        myMarker[2] = findViewById(R.id.tuesdayMarkerView);
        myMarker[3] = findViewById(R.id.wednesdayMarkerView);
        myMarker[4] = findViewById(R.id.thursdayMarkerView);
        myMarker[5] = findViewById(R.id.fridayMarkerView);
        myMarker[6] = findViewById(R.id.saturdayMarkerView);
        myLayout[0] = (LinearLayout)findViewById(R.id.sundayLinearLayout);
        myLayout[1] = (LinearLayout)findViewById(R.id.mondayLinearLayout);
        myLayout[2] = (LinearLayout)findViewById(R.id.tuesdayLinearLayout);
        myLayout[3] = (LinearLayout)findViewById(R.id.wednesdayLinearLayout);
        myLayout[4] = (LinearLayout)findViewById(R.id.thursdayLinearLayout);
        myLayout[5] = (LinearLayout)findViewById(R.id.fridayLinearLayout);
        myLayout[6] = (LinearLayout)findViewById(R.id.saturdayLinearLayout);
        myDateLayout[0] = (LinearLayout)findViewById(R.id.sundayHeaderLinearLayout);
        myDateLayout[1] = (LinearLayout)findViewById(R.id.mondayHeaderLinearLayout);
        myDateLayout[2] = (LinearLayout)findViewById(R.id.tuesdayHeaderLinearLayout);
        myDateLayout[3] = (LinearLayout)findViewById(R.id.wednesdayHeaderLinearLayout);
        myDateLayout[4] = (LinearLayout)findViewById(R.id.thursdayHeaderLinearLayout);
        myDateLayout[5] = (LinearLayout)findViewById(R.id.fridayHeaderLinearLayout);
        myDateLayout[6] = (LinearLayout)findViewById(R.id.saturdayHeaderLinearLayout);
    }

    private void getVars()
    {
        row_cnt = 0;
        density = getResources().getDisplayMetrics().density;
        myHelper = new TaskDbHelper(this);
        task_Id = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        scaleFactor = 1.0f;
    }

    private String getMonth( int mMonth )
    {
        String sMonth = "";
        if(mMonth == 1)
        sMonth = "JANUARY";
        else if(mMonth == 2)
            sMonth = "FEBRUARY";
        else if(mMonth == 3)
            sMonth = "MARCH";
        else if(mMonth == 4)
            sMonth = "APRIL";
        else if(mMonth == 5)
            sMonth = "MAY";
        else if(mMonth == 6)
            sMonth = "JUNE";
        else if(mMonth == 7)
            sMonth = "JULY";
        else if(mMonth == 8)
            sMonth = "AUGUST";
        else if(mMonth == 9)
            sMonth = "SEPTEMBER";
        else if(mMonth == 10)
            sMonth = "OCTOBER";
        else if(mMonth == 11)
            sMonth = "NOVEMBER";
       else
        {
            sMonth = "DECEMBER";
        }

//        switch( mMonth+1 )
//        {
//            case 1: sMonth = "JANUARY";   break;
//            case 2: sMonth = "FEBRUARY";   break;
//            case 3: sMonth = "MARCH";   break;
//            case 4: sMonth = "APRIL";   break;
//            case 5: sMonth = "MAY";   break;
//            case 6: sMonth = "JUNE";   break;
//            case 7: sMonth = "JULY";   break;
//            case 8: sMonth = "AUGUST";   break;
//            case 9: sMonth = "SEPTEMBER";   break;
//            case 10: sMonth = "OCTOBER";   break;
//            case 11: sMonth = "NOVEMBER";   break;
//            case 12: sMonth = "DECEMBER";   break;
//        }
        return sMonth;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weekly_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override   //Done
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if( v == myDateLayout[0] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[0]));
            finish();
        }
        else if( v == myDateLayout[1] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[1]));
            finish();
        }
        else if( v == myDateLayout[2] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[2]));
            finish();
        }
        else if( v == myDateLayout[3] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[3]));
            finish();
        }
        else if( v == myDateLayout[4] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[4]));
            finish();
        }
        else if( v == myDateLayout[5] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[5]));
            finish();
        }
        else if( v == myDateLayout[6] ) {
            setResult(RESULT_FIRST_USER, new Intent().putExtra("TASK_DATE", dateString[6]));
            finish();
        }
        else {
            int z;
            for( z = 0; v != message[z]; z++ );
            String msg = "" + task_Id.get(z);
            Intent i = new Intent(getApplicationContext(), ViewTaskActivity.class);
            i.putExtra("TASK_ID", msg);
            startActivityForResult(i, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_CANCELED ){
            Intent i = new Intent(getApplicationContext(), WeeklyViewActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void getTaskDetails() {
        SQLiteDatabase dbase = myHelper.getReadableDatabase();

        String selectQuery = "SELECT "
                + TaskContract.TaskEntry.COL_TASK_ID + ", "
                + TaskContract.TaskEntry.COL_TASK_TITLE + ", "
                + TaskContract.TaskEntry.COL_TASK_TYPE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDATE + ", "
                + TaskContract.TaskEntry.COL_TASK_DUEDAY
                + " FROM " + TaskContract.TaskEntry.TABLE
                + " WHERE " + TaskContract.TaskEntry.COL_TASK_DUEDATE + " BETWEEN "
                + "\"" + sDate + " 00:00:00\" AND \"" + eDate + " 23:59:59\""
                + " ORDER BY " + TaskContract.TaskEntry.COL_TASK_DUEDATE
                + ";";

        Cursor cursor = dbase.rawQuery(selectQuery, null);
        row_cnt = cursor.getCount();
        message = new TextView[row_cnt];
        int col = 0;
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                task_Id.add( cursor.getString(0) );
                message[i] = new TextView(WeeklyViewActivity.this);
                message[i].setText( cursor.getString(1) );
                message[i].setTextSize(15);
                message[i].setTypeface(Typeface.DEFAULT_BOLD);
                message[i].setTextColor(getResources().getColor(R.color.white));
                message[i].setGravity(Gravity.CENTER);
                int px = 0;
                if( cursor.getString(1).length() < 15 )
                    px = (int)(70 * density);
                else if( cursor.getString(1).length() < 23 )
                    px = (int)(100 * density);
                else
                    px = (int)(140 * density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px);
                params.setMargins(10,0,10,10);
                message[i].setLayoutParams(params);
                message[i].setPadding(0,10,0,10);
                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( 10f );
                switch (col)
                {
                    case 0: shape.setColor(getResources().getColor(R.color.color1));   break;
                    case 1: shape.setColor(getResources().getColor(R.color.color2));   break;
                    case 2: shape.setColor(getResources().getColor(R.color.color3));   break;
                    case 3: shape.setColor(getResources().getColor(R.color.color4));   break;
                    case 4: shape.setColor(getResources().getColor(R.color.color5));   break;
                }
                col = ( col + 1 ) % 5;
                message[i].setBackground(shape);

                int layoutId = 0;

                if(cursor.getString(4)== "Sun")
                    layoutId = 0;
                else if(cursor.getString(4)== "Mon")
                    layoutId = 1;
                else if(cursor.getString(4)== "Tue")
                    layoutId = 2;
                else if(cursor.getString(4)== "Wed")
                    layoutId = 3;
                else if(cursor.getString(4)== "Thu")
                    layoutId = 4;
                else if(cursor.getString(4)== "Fri")
                    layoutId = 5;
                else {
                    layoutId = 6;
                }
//                switch( cursor.getString(4) )
//                {
//                    case "Sun": layoutId = 0;   break;
//                    case "Mon": layoutId = 1;   break;
//                    case "Tue": layoutId = 2;   break;
//                    case "Wed": layoutId = 3;   break;
//                    case "Thu": layoutId = 4;   break;
//                    case "Fri": layoutId = 5;   break;
//                    case "Sat": layoutId = 6;   break;
//                }
                myLayout[layoutId].addView(message[i]);
                message[i].setOnClickListener(this);
                i++;
            } while (cursor.moveToNext());
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
            if (prediction.score > 3.5 && prediction.name.toLowerCase().equals("c")) {
                //Toast.makeText(this, prediction.name + " - score:" + prediction.score, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivityForResult(i, 1);
            }
            else if (prediction.score > 5.0 && prediction.name.toLowerCase().equals("right_swipe"))
            {
                setResult(RESULT_OK, null);
                finish();
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
                setResult(RESULT_OK, null);
                finish();
            }
            else if( scaleFactor < 1 )
            {
                //go to calendar view
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

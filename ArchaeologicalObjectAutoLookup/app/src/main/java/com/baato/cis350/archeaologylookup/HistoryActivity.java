// History Screen
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
public class HistoryActivity extends AppCompatActivity
{

    private float x1 = (float) 0.0, x2 = (float) 0.0;

    HistoryHelper mydb;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mydb = new HistoryHelper(this);


        lv = (ListView) findViewById(R.id.listView);
        loadList();
        lv.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                switch (event.getAction())
                {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x2 - x1;

                    if (Math.abs(deltaX) > 150)
                    {
                        // Left to Right swipe action
                        if (x2 < x1)
                        {
                            Intent intent =
                                    new Intent(getApplicationContext(), CameraUiActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }


                    }

                    break;
                }
                return false;
            }
        });
    }

    private void loadList()
    {
        Cursor resultSet = mydb.getDataSearch(1);


        //add items to the list
        String[] items = {};
        ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(items));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);

        lv.setAdapter(adapter);


        while (resultSet.moveToNext())
        {
            String history = "";
            // System.out.println(resultSet.getString(0));
            history += (resultSet.getString(0));
            history += (" " + resultSet.getString(1));
            itemList.add(history);
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        switch (event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
            x1 = event.getX();
            break;
        case MotionEvent.ACTION_UP:
            x2 = event.getX();
            float deltaX = x2 - x1;

            if (Math.abs(deltaX) > 150)
            {


                // Right to left swipe action
                if (x2 < x1)
                {
                    Intent intent = new Intent(this, CameraUiActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }

            }

            break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed()
    {
    }

    public void clearHistory(View v)
    {
        mydb.clearHistory();
        loadList();
    }
}

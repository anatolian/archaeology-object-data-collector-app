// Favorites Screen
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archaeologylookup;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
public class FavoriteActivity extends AppCompatActivity
{
    /**
     * User pressed back
     */
    @Override
    public void onBackPressed()
    {
    }

    /**
     * Activity restarted
     */
    @Override
    public void onResume()
    {
        while (resultSet.moveToNext())
        {
            String history = "";
            history += (resultSet.getString(2));
            itemList.add(history);
        }
        adapter.notifyDataSetChanged();
        super.onResume();
    }
    private float x1 = (float) 0.0, x2 = (float) 0.0;
    private ArrayList<String> itemList;
    private Cursor resultSet;
    private ArrayAdapter<String> adapter;
    /**
     * Activity launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        HistoryHelper mydb = new HistoryHelper(this);
        resultSet = mydb.getDataFav(1);
        String[] items = {};
        itemList = new ArrayList<String>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnTouchListener(new View.OnTouchListener()
        {
            /**
             * User touched list
             * @param v - element touched
             * @param event - touch event
             * @return Returns whether the event was handled
             */
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
                            if (x2 > x1)
                            {
                                Intent intent = new Intent(getApplicationContext(), CameraUiActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_right,
                                        R.anim.anim_slide_out_right);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * User pressed an item
             * @param parent - container view
             * @param view - selected item
             * @param position - item position
             * @param id - item id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                Object o = parent.getItemAtPosition(position);
                intent.putExtra("search", parent.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
        while (resultSet.moveToNext())
        {
            String history = "";
            history += (resultSet.getString(2));
            itemList.add(history);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * User pressed screen
     * @param event - touch event
     * @return Returns whether the event was handled
     */
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
                    if (x1 < x2)
                    {
                        Intent intent = new Intent(this, CameraUiActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
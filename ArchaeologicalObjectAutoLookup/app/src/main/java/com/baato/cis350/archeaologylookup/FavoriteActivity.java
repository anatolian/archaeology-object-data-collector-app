// Favorites Screen
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
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

    @Override
    public void onBackPressed()
    {
    }

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
                            Intent intent =
                                    new Intent(getApplicationContext(), CameraUiActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_right,
                                    R.anim.anim_slide_out_right);
                        }
                    }
                    else
                    {
                        // consider as something else - a screen tap for example
                    }
                    break;
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
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
            else
            {
                // consider as something else - a screen tap for example
            }
            break;
        }
        return super.onTouchEvent(event);
    }
}

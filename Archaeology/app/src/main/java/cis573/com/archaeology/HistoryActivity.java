// History Screen
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
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
    HistoryHelper myDB;
    ListView lv;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        myDB = new HistoryHelper(this);
        lv = (ListView) findViewById(R.id.listView);
        loadList();
        lv.setOnTouchListener(new View.OnTouchListener()
        {
            /**
             * User touched list
             * @param v - item
             * @param event - touch event
             * @return Returns whether the touch was handled
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
                            if (x2 < x1)
                            {
                                Intent intent = new Intent(getApplicationContext(), CameraUIActivity.class);
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

    /**
     * Load the list
     */
    private void loadList()
    {
        Cursor resultSet = myDB.getDataSearch(1);
        // add items to the list
        String[] items = {};
        ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(items));
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        lv.setAdapter(adapter);
        while (resultSet.moveToNext())
        {
            String history = "";
            history += (resultSet.getString(0));
            history += (" " + resultSet.getString(1));
            itemList.add(history);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * User touched screen
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
                    if (x2 < x1)
                    {
                        Intent intent = new Intent(this, CameraUIActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * User pressed back
     */
    @Override
    public void onBackPressed()
    {
    }

    /**
     * Clear history
     * @param v - button
     */
    public void clearHistory(View v)
    {
        myDB.clearHistory();
        loadList();
    }
}
// Search for an artifact
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.ui;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.archaeology.R;
import com.archaeology.util.HistoryHelper;
public class SearchActivity extends AppCompatActivity
{
    private String s;
    private FloatingActionButton fab;
    /**
     * Activity created
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle searchBundle = getIntent().getExtras();
        s = searchBundle.getString("search");
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(s);
        HistoryHelper db = new HistoryHelper(this);
        Cursor resultSet = db.getDataFav();
        boolean bookmarked = false;
        while (resultSet.moveToNext())
        {
            String x2 = resultSet.getString(2);
            if (s.equals(x2))
            {
                bookmarked = true;
            }
        }
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        if (bookmarked)
        {
            displayTrashcan();
        }
        else
        {
            displayStar();
        }
    }

    /**
     * Draw trashcan
     */
    private void displayTrashcan()
    {
        fab.setImageResource(R.drawable.delete);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked trashcan
             * @param v - trashcan
             */
            @Override
            public void onClick(View v)
            {
                HistoryHelper db = new HistoryHelper(getApplicationContext());
                db.removeBookmarkByItem(s);
                displayStar();
            }
        });
    }

    /**
     * Draw star
     */
    private void displayStar()
    {
        fab.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked star
             * @param v - star
             */
            @Override
            public void onClick(View v)
            {
                HistoryHelper db = new HistoryHelper(getApplicationContext());
                db.insertFav(s);
                displayTrashcan();
            }
        });
    }
}
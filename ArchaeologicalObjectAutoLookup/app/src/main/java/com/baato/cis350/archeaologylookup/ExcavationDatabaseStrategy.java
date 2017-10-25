// Strategy Pattern for Excavation Database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
public class ExcavationDatabaseStrategy extends AppCompatActivity implements Strategy
{
    TextView text;
    /**
     * Constructor
     * @param searchBundle - search data
     */
    public ExcavationDatabaseStrategy(Bundle searchBundle)
    {
    }

    /**
     * Draw the view
     */
    public void displayView()
    {
        text = new TextView(this);
        SqlRetriever retriever = new SqlRetriever();
        String[] arr = retriever.search("634110");
        StringBuilder sb = new StringBuilder("");
        for (String s: arr)
        {
            sb.append(s).append("\n");
        }

        text.setText(sb.toString());
    }

    /**
     * Add favorite
     * @param activity - calling activity
     */
    public void insertFavorite(Activity activity)
    {
        // TODO: unimplemented
    }
}
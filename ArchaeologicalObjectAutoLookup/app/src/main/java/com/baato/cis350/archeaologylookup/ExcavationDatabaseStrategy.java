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

    public ExcavationDatabaseStrategy(Bundle searchBundle)
    {
    }

    public void displayView()
    {
        text = new TextView(this);

        SqlRetreiver retriever = new SqlRetreiver();
        String[] arr = retriever.search("634110");

        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < arr.length; i++)
        {
            sb.append(arr[i] + "\n");
        }

        text.setText(sb.toString());
    }

    public void insertFavorite(Activity activity)
    {
        // unimplemented
    }
}

// Strategy pattern for museum artifact
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class PennMuseumArtifactStrategy extends AppCompatActivity implements Strategy
{
    String searchitem;
    String searchnumber;
    String searchdesc;
    String searchprov;
    String searchmat;
    String searchcur;
    String s;
    /**
     * Constructor
     * @param searchBundle - app parameters
     */
    public PennMuseumArtifactStrategy(Bundle searchBundle)
    {
        searchitem = searchBundle.getString("searchname");
        searchnumber = searchBundle.getString("searchnumber");
        searchdesc = searchBundle.getString("searchdescription");
        searchprov = searchBundle.getString("searchprovenience");
        searchmat = searchBundle.getString("searchmaterial");
        searchcur = searchBundle.getString("searchcuratorial_section");
        s = searchBundle.getString("search");
    }

    /**
     * Draw the view
     */
    public void displayView()
    {
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(s);
    }

    /**
     * Add favorite
     * @param activity - calling activity
     */
    public void insertFavorite(Activity activity)
    {
        HistoryHelper myDatabase = new HistoryHelper(activity);
        myDatabase.insertFav(searchnumber, searchitem, s, searchdesc, searchprov, searchmat, searchcur);
    }
}
// Retreiver implementation for SQL databases
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.os.AsyncTask;
import android.os.StrictMode;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
public class SqlRetriever implements Retriever
{
    public static final String URI =
            "jdbc:postgresql://ec2-23-21-76-49.compute-1.amazonaws.com:5432/dd1f9lfktc588d";
    public static final String USERNAME = "jwyawilfhcwmcz";
    public static final String PASSWORD =
            "bc077179bcbfc41e2bf79a47a7cea4b7f013d47b1b884c5339875adb880c38b9";
    /**
     * No need to access SQL db as an InputStream; can just open a connection to it
     * @param location - db location
     */
    @Override
    public InputStream retrieve(String location)
    {
        return null;
    }

    /**
     * No need to call this (you don't need to download a SQL db; just open a connection to it)
     * @param url - download url
     */
    @Override
    public void download(String url)
    {
    }

    /**
     * Opens connection to db and searches for object with id = item
     * @param item - key
     * @return Returns values
     */
    @Override
    public String[] search(final String item)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String[] result = new String[5];
        try
        {
            Class.forName("org.postgresql.Driver");
            Properties props = new Properties();
            props.setProperty("user", USERNAME);
            props.setProperty("password", PASSWORD);
            props.setProperty("sslmode", "require");
            Connection conn = DriverManager.getConnection(URI, props);
            System.out.println("DB connected");
            Statement st = conn.createStatement();
            // For SQL db we set up for testing/demonstration, key column is "easting"
            // Change "easting" to whatever the title of the "object_name" column of your db is
            ResultSet rs = st.executeQuery("SELECT * FROM archaeology_example WHERE easting = " + item);
            while (rs.next())
            {
//                result[0] = new String[5];
                for (int i = 0; i < 5; i++)
                {
                    System.out.println(rs.getString(i + 1));
                    result[i] = rs.getString(i + 1);
                }
            }
            rs.close();
            st.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Run from command line
     * @param args - command line arguments
     */
    public static void main(String[] args)
    {
        SqlRetriever sqlRetriever = new SqlRetriever();
        System.out.println(Arrays.deepToString(sqlRetriever.search("634110")));
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String[]>
    {
        private String item;
        private Exception exception;
        /**
         * Receive feedback in background
         * @param item - item to fetch
         */
        public RetrieveFeedTask(String item)
        {
            this.item = item;
        }

        /**
         * Run in background
         * @param voids - arguments
         * @return Returns the results
         */
        protected String[] doInBackground(Void... voids)
        {
            try
            {
//                URL url = new URL(urls[0]);
                String[] result = null;
                try
                {
                    Class.forName("org.postgresql.Driver");
                    Properties props = new Properties();
                    props.setProperty("user", USERNAME);
                    props.setProperty("password", PASSWORD);
                    props.setProperty("sslmode", "require");
                    Connection conn = DriverManager.getConnection(URI, props);
                    System.out.println("DB connected");
                    Statement st = conn.createStatement();
                    // For SQL db we set up for testing/demonstration, key column is "easting"
                    // Change "easting" to whatever the title of the "object_name" column of your
                    // db is
                    ResultSet rs = st.executeQuery("SELECT * FROM archaeology_example WHERE easting = " + item);
                    while (rs.next())
                    {
                        result = new String[5];
                        for (int i = 0; i < 5; i++)
                        {
                            result[i] = rs.getString(i + 1);
                        }
                    }
                    rs.close();
                    st.close();
                }
                catch (ClassNotFoundException | SQLException e)
                {
                    e.printStackTrace();
                }
                return result;
            }
            catch (Exception e)
            {
                this.exception = e;
                return null;
            }
        }

        /**
         * POST executed
         * @param strarr - POST arguments
         */
        protected void onPostExecute(String[] strarr)
        {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}
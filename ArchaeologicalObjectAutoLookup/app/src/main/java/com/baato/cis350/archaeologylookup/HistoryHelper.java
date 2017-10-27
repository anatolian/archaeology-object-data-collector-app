// SQLite Wrapper for HistoryActivity
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class HistoryHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "History.db";
    public static final String HISTORY_TABLE_NAME = "searchhistory";
    public static final String HISTORY_COLUMN_SEARCH = "search";
    public static final String HISTORY_COLUMN_ITEM = "searchitem";
    public static final String HISTORY_COLUMN_URL = "searchurl";
    public static final String HISTORY_COLUMN_ID = "id";
    public static final String HISTORY_COLUMN_DESCRIPTION = "searchdescription";
    public static final String HISTORY_COLUMN_PROVENIENCE = "searchprovenience";
    public static final String HISTORY_COLUMN_MATERIAL = "searchmaterial";
    public static final String HISTORY_COLUMN_CURATORIAL = "searchcuratorial_section";
    public static final String FAVORITE_TABLE_NAME = "favoritehistory";
    public static final String FAVORITE_COLUMN_SEARCH = "favorite";
    public static final String FAVORITE_COLUMN_ITEM = "favoriteitem";
    public static final String FAVORITE_COLUMN_URL = "favoriteurl";
    public static final String FAVORITE_COLUMN_DESCRIPTION = "favoritedescription";
    public static final String FAVORITE_COLUMN_PROVENIENCE = "favoriteprovenience";
    public static final String FAVORITE_COLUMN_MATERIAL = "favoritematerial";
    public static final String FAVORITE_COLUMN_CURATORIAL = "favoritecuratorial_section";
    public static final String FAVORITE_COLUMN_ID = "fav_id";
    /**
     * Constructor
     * @param context - calling context
     */
    public HistoryHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 6);
    }

    /**
     * OpenSQL connection opened
     * @param db - database
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + HISTORY_TABLE_NAME + " ( " + HISTORY_COLUMN_ID +
                " integer primary key, " + HISTORY_COLUMN_SEARCH + " text, " + HISTORY_COLUMN_ITEM +
                " text, " + HISTORY_COLUMN_URL + " text, " + HISTORY_COLUMN_DESCRIPTION +
                " text, " + HISTORY_COLUMN_PROVENIENCE + " text, " + HISTORY_COLUMN_MATERIAL +
                " text, " + HISTORY_COLUMN_CURATORIAL + " text )");
        db.execSQL("create table " + FAVORITE_TABLE_NAME + " ( " + FAVORITE_COLUMN_ID +
                " integer primary key, " + FAVORITE_COLUMN_SEARCH + " text, " +
                FAVORITE_COLUMN_ITEM + " text, " + FAVORITE_COLUMN_URL + " text, " +
                FAVORITE_COLUMN_DESCRIPTION + " text, " + FAVORITE_COLUMN_PROVENIENCE + " text, " +
                FAVORITE_COLUMN_MATERIAL + " text, " + FAVORITE_COLUMN_CURATORIAL + " text )");
    }

    /**
     * Upgrade database
     * @param db - database
     * @param oldVersion - old version
     * @param newVersion - new version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS searchhistory");
        db.execSQL("DROP TABLE IF EXISTS favoritehistory");
        onCreate(db);
    }

    /**
     * SQL INSERT
     * @param search - query
     * @param name - item name
     * @param url - location
     * @param desc - description
     * @param prov - provenience
     * @param mat - material
     * @param cur - curatorial section
     * @return Returns true
     */
    public boolean insertSearch(String search, String name, String url, String desc, String prov,
                                String mat, String cur)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.print((int) DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME));
        ContentValues contentValues = new ContentValues();
        contentValues.put("search", search);
        contentValues.put("searchitem", name);
        contentValues.put("searchurl", url);
        contentValues.put("searchdescription", desc);
        contentValues.put("searchprovenience", prov);
        contentValues.put("searchmaterial", mat);
        contentValues.put("searchcuratorial_section", cur);
        db.insert("searchhistory", null, contentValues);
        return true;
    }

    /**
     * Add favorite
     * @param favorite - favorite item
     * @param name - name of item
     * @param url - location
     * @param desc - description
     * @param prov - provenience
     * @param mat - material
     * @param cur - curatorial section
     * @return Returns true
     */
    public boolean insertFav(String favorite, String name, String url, String desc, String prov,
                             String mat, String cur)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.print((int) DatabaseUtils.queryNumEntries(db, FAVORITE_TABLE_NAME));
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorite", favorite);
        contentValues.put("favoriteitem", name);
        contentValues.put("favoriteurl", url);
        contentValues.put("favoritedescription", desc);
        contentValues.put("favoriteprovenience", prov);
        contentValues.put("favoritematerial", mat);
        contentValues.put("favoritecuratorial_section", cur);
        db.insert("favoritehistory", null, contentValues);
        return true;
    }

    /**
     * SQL SELECT
     * @param id - item id
     * @return Returns the result
     */
    public Cursor getDataSearch(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println("db");
        System.out.println((int) DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME));
        System.out.println("db");
        return db.rawQuery("SELECT search,searchitem,searchurl,searchdescription" +
                        ",searchprovenience,searchmaterial,searchcuratorial_section FROM " +
                        "searchhistory", null);
    }

    /**
     * Fetch favorites
     * @param id - item id
     * @return Returns the entries
     */
    public Cursor getDataFav(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println("db");
        System.out.println((int) DatabaseUtils.queryNumEntries(db, FAVORITE_TABLE_NAME));
        System.out.println("db");
        return db.rawQuery("SELECT favorite,favoriteitem,favoriteurl,favoritedescription," +
                        "favoriteprovenience,favoritematerial,favoritecuratorial_section FROM " +
                        "favoritehistory", null);
    }

    /**
     * SQL DELETE history
     */
    public void clearHistory()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM searchhistory");
    }

    /**
     * SQL DELETE favoritehistory
     * @param item - item id
     */
    public void removeBookmarkByItem(String item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM favoritehistory where favoriteurl='" + item + "'");
    }
}
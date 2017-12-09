// SQLite Wrapper for HistoryActivity
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology.util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class HistoryHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "History.db";
    private static final String HISTORY_TABLE_NAME = "searchhistory";
    private static final String HISTORY_COLUMN_SEARCH = "search";
    private static final String HISTORY_COLUMN_ITEM = "searchitem";
    private static final String HISTORY_COLUMN_URL = "searchurl";
    private static final String HISTORY_COLUMN_ID = "id";
    private static final String HISTORY_COLUMN_DESCRIPTION = "searchdescription";
    private static final String HISTORY_COLUMN_PROVENIENCE = "searchprovenience";
    private static final String HISTORY_COLUMN_MATERIAL = "searchmaterial";
    private static final String HISTORY_COLUMN_CURATORIAL = "searchcuratorial_section";
    private static final String FAVORITE_TABLE_NAME = "favoritehistory";
    private static final String FAVORITE_COLUMN_SEARCH = "favorite";
    private static final String FAVORITE_COLUMN_ITEM = "favoriteitem";
    private static final String FAVORITE_COLUMN_URL = "favoriteurl";
    private static final String FAVORITE_COLUMN_DESCRIPTION = "favoritedescription";
    private static final String FAVORITE_COLUMN_PROVENIENCE = "favoriteprovenience";
    private static final String FAVORITE_COLUMN_MATERIAL = "favoritematerial";
    private static final String FAVORITE_COLUMN_CURATORIAL = "favoritecuratorial_section";
    private static final String FAVORITE_COLUMN_ID = "fav_id";
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
        db.execSQL("CREATE TABLE " + HISTORY_TABLE_NAME + " ( " + HISTORY_COLUMN_ID +
                " integer primary key, " + HISTORY_COLUMN_SEARCH + " text, " + HISTORY_COLUMN_ITEM
                + " text, " + HISTORY_COLUMN_URL + " text, " + HISTORY_COLUMN_DESCRIPTION +
                " text, " + HISTORY_COLUMN_PROVENIENCE + " text, " + HISTORY_COLUMN_MATERIAL +
                " text, " + HISTORY_COLUMN_CURATORIAL + " text )");
        db.execSQL("CREATE TABLE " + FAVORITE_TABLE_NAME + " ( " + FAVORITE_COLUMN_ID +
                " INTEGER PRIMARY KEY, " + FAVORITE_COLUMN_SEARCH + " TEXT, " +
                FAVORITE_COLUMN_ITEM + " TEXT, " + FAVORITE_COLUMN_URL + " TEXT, " +
                FAVORITE_COLUMN_DESCRIPTION + " TEXT, " + FAVORITE_COLUMN_PROVENIENCE + " TEXT, " +
                FAVORITE_COLUMN_MATERIAL + " TEXT, " + FAVORITE_COLUMN_CURATORIAL + " TEXT )");
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
     * @param URL - location
     * @param desc - description
     * @param prov - provenience
     * @param mat - material
     * @param cur - curatorial section
     */
    public void insertSearch(String search, String name, String URL, String desc, String prov,
                             String mat, String cur)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.print((int) DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME));
        ContentValues contentValues = new ContentValues();
        contentValues.put("search", search);
        contentValues.put("searchitem", name);
        contentValues.put("searchurl", URL);
        contentValues.put("searchdescription", desc);
        contentValues.put("searchprovenience", prov);
        contentValues.put("searchmaterial", mat);
        contentValues.put("searchcuratorial_section", cur);
        db.insert("searchhistory", null, contentValues);
    }

    /**
     * Add favorite
     * @param URL - location
     */
    public void insertFav(String URL)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.print((int) DatabaseUtils.queryNumEntries(db, FAVORITE_TABLE_NAME));
        ContentValues contentValues = new ContentValues();
        contentValues.put("favorite", "");
        contentValues.put("favoriteitem", "");
        contentValues.put("favoriteurl", URL);
        contentValues.put("favoritedescription", "");
        contentValues.put("favoriteprovenience", "");
        contentValues.put("favoritematerial", "");
        contentValues.put("favoritecuratorial_section", "");
        db.insert("favoritehistory", null, contentValues);
    }

    /**
     * SQL SELECT
     * @return Returns the result
     */
    public Cursor getDataSearch()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println("db");
        System.out.println((int) DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME));
        System.out.println("db");
        return db.rawQuery("SELECT search, searchitem, searchurl, searchdescription," +
                "searchprovenience, searchmaterial, searchcuratorial_section FROM " +
                "searchhistory", null);
    }

    /**
     * Fetch favorites
     * @return Returns the entries
     */
    public Cursor getDataFav()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        System.out.println("db");
        System.out.println((int) DatabaseUtils.queryNumEntries(db, FAVORITE_TABLE_NAME));
        System.out.println("db");
        return db.rawQuery("SELECT favorite, favoriteitem, favoriteurl, favoritedescription," +
                "favoriteprovenience, favoritematerial, favoritecuratorial_section FROM " +
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
        db.execSQL("DELETE FROM favoritehistory WHERE favoriteurl = '" + item + "'");
    }
}
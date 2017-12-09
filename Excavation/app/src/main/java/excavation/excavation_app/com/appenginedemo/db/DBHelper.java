// Connect to database
package excavation.excavation_app.com.appenginedemo.db;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper
{
    private static DBHelper dbHelper;
    private SQLiteDatabase database;
    private final static String DB_NAME = "user.db";
    private final static int DB_VERSION = 3;
    private static final String TABLE_SERVER_DETAIL = "user_detail";
    private static final String IP_ADDRESS = "ip_address";
    private static final String DATABASE_SERVER = "create table " + TABLE_SERVER_DETAIL + "(" +
            IP_ADDRESS + " text);";
    private static final String TABLE_IMAGE_PROPERTY = "image_property";
    // IMAGE_PROPERTY TABLE COLUMNS
    private static final String CONTEXT_SUBPATH_3D = "context_subpath_3d";
    private static final String BASE_IMAGE_PATH = "base_image_path";
    private static final String CONTEXT_SUBPATH = "context_subpath";
    private static final String AREA_DIVIDER = "sample_label_area_divider";
    private static final String CONTEXT_DIVIDER = "sample_label_context_divider";
    private static final String SAMPLE_LABEL_FONT = "sample_label_font";
    private static final String SAMPLE_LABEL_FONT_SIZE = "sample_label_font_size";
    private static final String SAMPLE_LABEL_PLACEMENT = "sample_label_placement";
    private static final String SAMPLE_DIVIDER = "sample_label_sample_divider";
    private static final String SAMPLE_SUBPATH = "sample_subpath";
    private static final String CREATE_TABLE_IMAGE_PROPERTY = "create table " +
            TABLE_IMAGE_PROPERTY + "(" + CONTEXT_SUBPATH_3D + " text," + BASE_IMAGE_PATH +
            " text," + CONTEXT_SUBPATH +" text," + AREA_DIVIDER + " text," + CONTEXT_DIVIDER +
            " text," + SAMPLE_LABEL_FONT + " text," + SAMPLE_LABEL_FONT_SIZE + " text," +
            SAMPLE_LABEL_PLACEMENT + " text," + SAMPLE_DIVIDER + " text," + SAMPLE_SUBPATH +
            " text);";

    /**
     * Constructor
     * @param context - calling context
     * @param name - db name
     * @param factory - db reader
     * @param version - db version
     */
    public DBHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    /**
     * Delete detail
     */
    public void deleteServerDetail()
    {
        database.delete(TABLE_SERVER_DETAIL, null, null);
    }

    /**
     * Activity lanched
     * @param db - SQLite DB
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DATABASE_SERVER);
        db.execSQL(CREATE_TABLE_IMAGE_PROPERTY);
    }

    /**
     * Upgrade DB version
     * @param db - DB to upgrade
     * @param oldVersion - old db version
     * @param newVersion - new db version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_PROPERTY);
        onCreate(db);
    }

    /**
     * Connect to server
     * @param d - server IP
     */
    public void addServerAddress(String d)
    {
        ContentValues values = new ContentValues();
        values.put(IP_ADDRESS, d);
        database.insert(TABLE_SERVER_DETAIL, null, values);
    }

    /**
     * Get server IP address
     * @return Returns the server IP address
     */
    public String getIPAddress()
    {
        String d = "";
        Cursor cursor = database.query(TABLE_SERVER_DETAIL, new String[] {IP_ADDRESS},
                null, null, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast())
        {
            d = cursor.getString(0);
            cursor.close();
            return d;
        }
        cursor.close();
        return d;
    }

    /**
     * Add image property
     * @param data - property
     * @return Returns whether the update succeeded
     */
    public boolean addImageProperty(ImagePropertyBean data)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(CONTEXT_SUBPATH_3D, data.contextSubPath3D);
            values.put(BASE_IMAGE_PATH, data.baseImagePath);
            values.put(CONTEXT_SUBPATH, data.contextSubPath);
            values.put(AREA_DIVIDER, data.sampleLabelAreaDivider);
            values.put(CONTEXT_DIVIDER, data.sampleLabelContextDivider);
            values.put(SAMPLE_LABEL_FONT, data.sampleLabelFont);
            values.put(SAMPLE_LABEL_FONT_SIZE, data.sampleLabelFontSize);
            values.put(SAMPLE_LABEL_PLACEMENT, data.sampleLabelPlacement);
            values.put(SAMPLE_DIVIDER, data.sampleLabelSampleDivider);
            values.put(SAMPLE_SUBPATH, data.sampleSubPath);
            database.insert(TABLE_IMAGE_PROPERTY, null, values);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Delete image property
     */
    public void deleteImageProperty()
    {
        try
        {
            database.delete(TABLE_IMAGE_PROPERTY, null, null);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Change image property
     * @param threeDSubPath - path to property
     * @param baseImagePath - image location
     * @param contextSubPath - context location
     * @param areaDivider - area divider
     * @param contextDivider - context divider
     * @param fontSize - font size
     * @param item - item to change
     * @param sampleDivider - divider
     * @param sampleSubPath - subpath
     * @return Returns whether the update succeeded
     */
    public boolean updateImageProperty(String threeDSubPath, String baseImagePath,
                                       String contextSubPath, String areaDivider,
                                       String contextDivider, String fontSize, String item,
                                       String sampleDivider, String sampleSubPath)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(CONTEXT_SUBPATH_3D, threeDSubPath);
            values.put(BASE_IMAGE_PATH, baseImagePath);
            values.put(CONTEXT_SUBPATH, contextSubPath);
            values.put(AREA_DIVIDER, areaDivider);
            values.put(CONTEXT_DIVIDER, contextDivider);
            values.put(SAMPLE_LABEL_FONT_SIZE, fontSize);
            values.put(SAMPLE_LABEL_PLACEMENT, item);
            values.put(SAMPLE_DIVIDER, sampleDivider);
            values.put(SAMPLE_SUBPATH,  sampleSubPath);
            database.update(TABLE_IMAGE_PROPERTY, values, null, null);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get an image property
     * @return Returns the image property
     */
    public ImagePropertyBean getImageProperty()
    {
        ImagePropertyBean data = new ImagePropertyBean();
        String sql = " select * from " + TABLE_IMAGE_PROPERTY;
        Cursor cur = database.rawQuery(sql, null);
        cur.moveToFirst();
        if (!cur.isAfterLast())
        {
            data.contextSubPath3D = cur.getString(0);
            data.baseImagePath = cur.getString(1);
            data.contextSubPath = cur.getString(2);
            data.sampleLabelAreaDivider = cur.getString(3);
            data.sampleLabelContextDivider = cur.getString(4);
            data.sampleLabelFont = cur.getString(5);
            data.sampleLabelFontSize = cur.getString(6);
            data.sampleLabelPlacement = cur.getString(7);
            data.sampleLabelSampleDivider = cur.getString(8);
            data.sampleSubPath = cur.getString(9);
            cur.close();
            return data;
        }
        cur.close();
        return data;
    }

    /**
     * Open DB
     */
    public void open()
    {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Close DB
     */
    public void close()
    {
        if (database != null)
        {
            database.close();
        }
    }

    /**
     * Get helper
     * @param context - calling context
     * @return Returns the singleton instance
     */
    public static DBHelper getInstance(Context context)
    {
        if (dbHelper == null)
        {
            dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        }
        return dbHelper;
    }
}
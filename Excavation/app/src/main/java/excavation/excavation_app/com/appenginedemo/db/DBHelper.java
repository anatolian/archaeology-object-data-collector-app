// Connect to database
// @author: anatolian
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
    static DBHelper dbHelper;
    SQLiteDatabase database;
    final static String DB_NAME = "user.db";
    final static int DB_VERSION = 3;
    static final String TABLE_SERVER_DETAIL = "user_detail";
    static final String IP_ADDRESS = "ip_address";
    static final String DATABASE_SERVER = "create table " + TABLE_SERVER_DETAIL + "(" + IP_ADDRESS + " text);";
    static final String TABLE_IMAGE_PROPERTY = "image_property";
    // IMAGE_PROPERTY TABLE COLUMNS
    static final String CONTEXT_SUBPATH_3D = "context_subpath_3d";
    static final String BASE_IMAGE_PATH = "base_image_path";
    static final String CONTEXT_SUBPATH = "context_subpath";
    static final String AREA_DIVIDER = "sample_label_area_divider";
    static final String CONTEXT_DIVIDER = "sample_label_context_divider";
    static final String SAMPLE_LABEL_FONT = "sample_label_font";
    static final String SAMPLE_LABEL_FONT_SIZE = "sample_label_font_size";
    static final String SAMPLE_LABEL_PLACEMENT = "sample_label_placement";
    static final String SAMPLE_DIVIDER = "sample_label_sample_divider";
    static final String SAMPLE_SUBPATH = "sample_subpath";
    static final String CREATE_TABLE_IMAGE_PROPERTY = "create table " + TABLE_IMAGE_PROPERTY
            + "(" + CONTEXT_SUBPATH_3D + " text," + BASE_IMAGE_PATH + " text," + CONTEXT_SUBPATH +" text,"
            + AREA_DIVIDER + " text," + CONTEXT_DIVIDER + " text," + SAMPLE_LABEL_FONT + " text,"
            + SAMPLE_LABEL_FONT_SIZE + " text," + SAMPLE_LABEL_PLACEMENT + " text,"
            + SAMPLE_DIVIDER + " text," + SAMPLE_SUBPATH + " text);";

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
     * @return Returns true
     */
    public boolean deleteServerDetail()
    {
        database.delete(TABLE_SERVER_DETAIL, null, null);
        return true;
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
     * @return Returns true
     */
    public boolean addServerAddress(String d)
    {
        ContentValues values = new ContentValues();
        values.put(IP_ADDRESS, d);
        database.insert(TABLE_SERVER_DETAIL, null, values);
        return true;
    }

    /**
     * Delete a user
     * @return Returns true
     */
    public boolean deleteUser()
    {
        database.delete(TABLE_SERVER_DETAIL, null, null);
        return true;
    }

    /**
     * Get server IP address
     * @return Returns the server IP address
     */
    public String getIpAddress()
    {
        String d = "";
        Cursor cursor = database.query(TABLE_SERVER_DETAIL, new String[] {IP_ADDRESS}, null,
                null, null, null, null);
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
            ContentValues values=new ContentValues();
            values.put(CONTEXT_SUBPATH_3D,data.context_subpath_3d);
            values.put(BASE_IMAGE_PATH, data.base_image_path);
            values.put(CONTEXT_SUBPATH,  data.context_subpath);
            values.put(AREA_DIVIDER,  data.sample_label_area_divider);
            values.put(CONTEXT_DIVIDER, data.sample_label_context_divider);
            values.put(SAMPLE_LABEL_FONT,  data.sample_label_font);
            values.put(SAMPLE_LABEL_FONT_SIZE, data.sample_label_font_size);
            values.put(SAMPLE_LABEL_PLACEMENT, data.sample_label_placement);
            values.put(SAMPLE_DIVIDER, data.sample_label_sample_divider);
            values.put(SAMPLE_SUBPATH,  data.sample_subpath);
            database.insert(TABLE_IMAGE_PROPERTY, null, values);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Delete image property
     * @return Returns whether the update succeeded
     */
    public boolean deleteImageProperty()
    {
        try
        {
            database.delete(TABLE_IMAGE_PROPERTY, null, null);
            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Change image property
     * @param _3dsubpath - path to property
     * @param base_image_path - image location
     * @param context_subpath - context location
     * @param area_divider - area divider
     * @param context_divider - context divider
     * @param font_size - font size
     * @param item - item to change
     * @param sample_divider - divider
     * @param sample_subpath - subpath
     * @return Returns whether the update succeeded
     */
    public boolean updateImageProperty(String _3dsubpath, String base_image_path,
                                       String context_subpath, String area_divider,
                                       String context_divider, String font_size, String item,
                                       String sample_divider, String sample_subpath)
    {
        try
        {
            ContentValues values = new ContentValues();
            values.put(CONTEXT_SUBPATH_3D,_3dsubpath);
            values.put(BASE_IMAGE_PATH, base_image_path);
            values.put(CONTEXT_SUBPATH, context_subpath);
            values.put(AREA_DIVIDER, area_divider);
            values.put(CONTEXT_DIVIDER, context_divider);
            values.put(SAMPLE_LABEL_FONT_SIZE, font_size);
            values.put(SAMPLE_LABEL_PLACEMENT, item);
            values.put(SAMPLE_DIVIDER, sample_divider);
            values.put(SAMPLE_SUBPATH,  sample_subpath);
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
        ImagePropertyBean data=new ImagePropertyBean();
        String sql = " select * from " + TABLE_IMAGE_PROPERTY;
        Cursor cur = database.rawQuery(sql,null);
        cur.moveToFirst();
        if (!cur.isAfterLast())
        {
            data.context_subpath_3d = cur.getString(0);
            data.base_image_path = cur.getString(1);
            data.context_subpath = cur.getString(2);
            data.sample_label_area_divider = cur.getString(3);
            data.sample_label_context_divider = cur.getString(4);
            data.sample_label_font = cur.getString(5);
            data.sample_label_font_size = cur.getString(6);
            data.sample_label_placement = cur.getString(7);
            data.sample_label_sample_divider = cur.getString(8);
            data.sample_subpath = cur.getString(9);
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
package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import data.model.LocationObj;
import logic.helpers.DataFormatConverter;
import logic.helpers.MyLogs;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sysDB";
    private static final int DATABASE_VERSION = 1;
    private static DataBase sInstance;

    // Table name
    private static final String TABLE_LOCATIONS = "locations_tab";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LNG = "lng";
    private static final String KEY_LAT = "lat";
    private static final String KEY_SELECTED = "selected";
    private static final String KEY_SYS_TIME = "log_time";


    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "(" +
            KEY_ID + " TEXT," + KEY_NAME + " TEXT," + KEY_COUNTRY + " TEXT," +
            KEY_LNG + " TEXT," + KEY_LAT + " TEXT," + KEY_SELECTED + " INTEGER," +
            KEY_SYS_TIME + "  DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))," +
            " PRIMARY KEY (" + KEY_ID + ")" + " )";


    private DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    /**
     * All CRUD Operations
     */

    public void insertUpdateLocation(LocationObj locationObj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, locationObj.getName());
            values.put(KEY_COUNTRY, locationObj.getCountry());
            values.put(KEY_LNG, locationObj.getLon());
            values.put(KEY_LAT, locationObj.getLat());

            int afectedRows = db.update(
                    TABLE_LOCATIONS,
                    values,
                    KEY_ID + " =?",
                    new String[]{locationObj.getId()});

            if (afectedRows == 0) {
                values.put(KEY_ID, locationObj.getId());
                db.insertOrThrow(TABLE_LOCATIONS, null, values);
                MyLogs.LOG("DataBase", "insertUpdateLocation", "inserted");

            } else {
                MyLogs.LOG("DataBase", "insertUpdateLocation", "updated");
            }

            db.setTransactionSuccessful();

        } catch (Throwable ex) {
            MyLogs.LOG("DataBase", "insertUpdateLocation", "ERROR --> ex: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            safeEndTransaction(db);
        }
    }

    public List<LocationObj> selectAllLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocationObj> list = new ArrayList<>();
        db.beginTransaction();

        try {
            Cursor cursor = db.query(TABLE_LOCATIONS,
                    new String[]{KEY_ID, KEY_NAME, KEY_COUNTRY, KEY_LNG, KEY_LAT, KEY_SELECTED},
                    null, null, null, null, KEY_SYS_TIME + " DESC ", null);

            if (cursor != null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    LocationObj locationObj = new LocationObj();
                    locationObj.setId(cursor.getString(0));
                    locationObj.setName(cursor.getString(1));
                    locationObj.setCountry(cursor.getString(2));
                    locationObj.setLon(DataFormatConverter.safeParseToDouble(cursor.getString(3)));
                    locationObj.setLat(DataFormatConverter.safeParseToDouble(cursor.getString(4)));
                    locationObj.setSelected(DataFormatConverter.isTrueStr(cursor.getString(5)));

                    list.add(locationObj);
                }

                cursor.close();
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            MyLogs.LOG("DataBase", "selectAllCountries", "ERROR --> ex: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            safeEndTransaction(db);
        }

        MyLogs.LOG("DataBase", "selectAllCountries", "SQL - get items count -> " + list.size());
        return list;
    }

    public void updateSelectedLocation(String selectedId) {
        MyLogs.LOG("DataBase", "updateSelectedLocation", "selectedId --> " + selectedId);
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();

        try {
            Cursor cursor = db.query(TABLE_LOCATIONS,
                    new String[]{KEY_ID, KEY_NAME, KEY_COUNTRY, KEY_LNG, KEY_LAT, KEY_SELECTED},
                    null, null, null, null, KEY_SYS_TIME + " DESC ", null);

            if (cursor != null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String curId = cursor.getString(0);

                    //locationObj
                    ContentValues values = new ContentValues();
                    int status = selectedId.equals(curId) ? 1 : 0;
                    values.put(KEY_SELECTED, status);

                    MyLogs.LOG("DataBase", "updateSelectedLocation", "curId --> " + curId + " status: " + status);

                    db.update(
                            TABLE_LOCATIONS,
                            values,
                            KEY_ID + " =?",
                            new String[]{curId});
                }

                cursor.close();
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            MyLogs.LOG("DataBase", "updateSelectedLocation", "ERROR --> ex: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            safeEndTransaction(db);
        }
    }


    public void deleteLocation(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            int deletedItems = db.delete(TABLE_LOCATIONS, KEY_ID + "=?", new String[]{id});
            MyLogs.LOG("DataBase", "deleteUserAllAccess", "deletedItems: " + deletedItems);

            db.setTransactionSuccessful();

        } catch (Throwable ex) {
            MyLogs.LOG("DataBase", "deleteUserAllAccess", "ERROR --> ex: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            safeEndTransaction(db);
        }
    }

    //-------------------------------- GENERIC HELPER stuff---------------------------------------//
    private void safeEndTransaction(SQLiteDatabase db) {
        try {
            db.endTransaction();

        } catch (Exception ex) {
            MyLogs.LOG("DataBase", "safeEndTransaction", "ERROR --> ex: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
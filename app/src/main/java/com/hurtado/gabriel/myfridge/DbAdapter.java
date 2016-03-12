package com.hurtado.gabriel.myfridge;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DbAdapter {

    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE= "date";

    private static final String TAG = "DbAdapter";
    private static final String DATABASE_NAME = "MyFridge";
    private static final String SQLITE_TABLE = "Fridge";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + "," +
                    KEY_DATE +
                    ");";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createDate(String name,String date) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DATE, date);

        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }

    public void deleteAll() {

         mDb.delete(SQLITE_TABLE, null, null);
    }

    public void delete(long rowId) {

        mDb.delete(SQLITE_TABLE,  KEY_ROWID + "=" + rowId , null);
    }

    public Cursor fetchAll() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_NAME, KEY_DATE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public String fetchName(Long id) {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_NAME, KEY_DATE},
                KEY_ROWID+"=?", new String[] { String.valueOf(id) }, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            String text=mCursor.getString(1);
            mCursor.close();
            return text;
        }

        return "";
    }

    public int updatedetails(long rowId,String name, String date)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ROWID, rowId);
        args.put(KEY_NAME, name);
        args.put(KEY_DATE, date);
        return mDb.update(SQLITE_TABLE, args, KEY_ROWID + "=" + rowId, null);
    }



    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }


}
package com.project.lyt.cyclenewwest.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to access SQLite for the Favourites DB.
 */
public class FavDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Favs.db";
    private static final int DB_VERSION = 1;

    private Context context;

    public FavDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(getCreateFavTableSql());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }

    /**
     * Creates SQL string to create
     * the favourites table in SQLite storage.
     * @return
     *      SQL creation statement string
     *
     */
    private String getCreateFavTableSql() {
        String sql = "";
        sql += "CREATE TABLE Fav (";
        sql += "id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "name TEXT);";

        return sql;
    }

    /**
     * Inserts a favourite trail into the database.
     * @param db
     *      The SQLite database.
     * @param favName
     *      The name of the favourite trail to add.
     */
    public void insertFav(SQLiteDatabase db, String favName) {
        ContentValues values = new ContentValues();
        values.put("name", favName);

        db.insert("Fav", null, values);
    }
}

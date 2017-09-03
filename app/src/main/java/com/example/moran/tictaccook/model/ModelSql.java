package com.example.moran.tictaccook.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moran on 01/08/2017.
 */

public class ModelSql extends SQLiteOpenHelper {

    public ModelSql(Context context) {
        super(context, "recipesDataBase.db", null, 27);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeSql.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipeSql.onUpgrade(db, oldVersion, newVersion);
    }
}

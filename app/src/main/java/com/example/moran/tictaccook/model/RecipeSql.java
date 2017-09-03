package com.example.moran.tictaccook.model;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import static com.example.moran.tictaccook.model.RecipeSql.RECIPE_LAST_UPDATE;

/**
 * Created by moran on 01/08/2017.
 */

public class RecipeSql {

    static final String RECIPE_TABLE = "recipes";
    static final String RECIPE_ID = "recipeId";
    static final String RECIPE_NAME = "name";
    static final String RECIPE_CHECK = "checked";
    static final String RECIPE_IMAGE_URL = "image";
    static final String RECIPE_INGREDIENTS = "ingredients";
    static final String RECIPE_METHOD = "method";
    static final String RECIPE_PREP_TIME = "preparationTime";
    static final String RECIPE_LAST_UPDATE = "lastUpdateDate";


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RECIPE_TABLE +
                " (" +
                RECIPE_ID + " TEXT PRIMARY KEY, " +
                RECIPE_NAME + " TEXT, " +
                RECIPE_CHECK + " NUMBER, " +
                RECIPE_IMAGE_URL + " TEXT, " +
                RECIPE_INGREDIENTS + " TEXT, " +
                RECIPE_METHOD + " TEXT, " +
                RECIPE_PREP_TIME + " TEXT, " +
                RECIPE_LAST_UPDATE + " NUMBER);"
        );
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + RECIPE_TABLE + ";");
        onCreate(db);
    }

    public static List<Recipe> getAllRecipes(SQLiteDatabase db) {
        Cursor cursor = db.query(RECIPE_TABLE, null, null, null, null, null, null);
        List<Recipe> list = new LinkedList<>();
        if (cursor.moveToFirst()) {
            /**
             * If we can move the cursor to the first row, it means we have at least one Recipe in the
             * list. Otherwise, we'll return just an empty list.
             */
            int idIndex = cursor.getColumnIndex(RECIPE_ID);
            int nameIndex = cursor.getColumnIndex(RECIPE_NAME);
            int checkedIndex = cursor.getColumnIndex(RECIPE_CHECK);
            int imageUrlIndex = cursor.getColumnIndex(RECIPE_IMAGE_URL);
            int ingIndex = cursor.getColumnIndex(RECIPE_INGREDIENTS);
            int methodIndex = cursor.getColumnIndex(RECIPE_METHOD);
            int prepTimeIndex = cursor.getColumnIndex(RECIPE_PREP_TIME);
            int lastUpdateDateIndex = cursor.getColumnIndex(RECIPE_LAST_UPDATE);

            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getString(idIndex));
                recipe.setName(cursor.getString(nameIndex));
                recipe.setChecked(cursor.getInt(checkedIndex) == 1);
                recipe.setImageUrl(cursor.getString(imageUrlIndex));
                recipe.setIngredients(cursor.getString(ingIndex));
                recipe.setMethod(cursor.getString(methodIndex));
                recipe.setPreparationTime(cursor.getString(prepTimeIndex));
                recipe.setLastUpdateDate(cursor.getDouble(lastUpdateDateIndex));

                list.add(recipe);
            } while (cursor.moveToNext());
        }
        Log.d("TAG", "RecipeSql : getAllRecipes : DATA!! : " + list.size());
        return list;
    }

    public static void addRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_ID, recipe.getId());
        values.put(RECIPE_NAME, recipe.getName());
        if (recipe.isChecked()) {
            values.put(RECIPE_CHECK, 1);
        } else {
            values.put(RECIPE_CHECK, 0);
        }
        values.put(RECIPE_IMAGE_URL, recipe.getImageUrl());
        values.put(RECIPE_INGREDIENTS, recipe.getIngredients());
        values.put(RECIPE_METHOD, recipe.getMethod());
        values.put(RECIPE_PREP_TIME, recipe.getPreparationTime());
        values.put(RECIPE_LAST_UPDATE, recipe.getLastUpdateDate());

        db.insert(RECIPE_TABLE, RECIPE_ID, values);
    }

    public static Recipe getRecipe(SQLiteDatabase db, String recipeId) {
        Cursor res = db.rawQuery("select * from " + RECIPE_TABLE + " where " + RECIPE_ID + "=" + recipeId + "", null);

        int idIndex = res.getColumnIndex(RECIPE_ID);
        int nameIndex = res.getColumnIndex(RECIPE_NAME);
        int checkedIndex = res.getColumnIndex(RECIPE_CHECK);
        int imageUrlIndex = res.getColumnIndex(RECIPE_IMAGE_URL);
        int ingIndex = res.getColumnIndex(RECIPE_INGREDIENTS);
        int methodIndex = res.getColumnIndex(RECIPE_METHOD);
        int prepTimeIndex = res.getColumnIndex(RECIPE_PREP_TIME);
        int lastUpdateDateIndex = res.getColumnIndex(RECIPE_LAST_UPDATE);

        if(res.moveToFirst()){
            Recipe recipe = new Recipe();
            recipe.setId(res.getString(idIndex));
            recipe.setName(res.getString(nameIndex));
            recipe.setChecked(res.getInt(checkedIndex) == 1);
            recipe.setImageUrl(res.getString(imageUrlIndex));
            recipe.setIngredients(res.getString(ingIndex));
            recipe.setMethod(res.getString(methodIndex));
            recipe.setPreparationTime(res.getString(prepTimeIndex));
            recipe.setLastUpdateDate(res.getDouble(lastUpdateDateIndex));
            return recipe;
        }
        return null;
    }

    public static void editRecipeDetails(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_ID, recipe.getId());
        values.put(RECIPE_NAME, recipe.getName());
        if (recipe.isChecked()) {
            values.put(RECIPE_CHECK, 1);
        } else {
            values.put(RECIPE_CHECK, 0);
        }
        values.put(RECIPE_IMAGE_URL, recipe.getImageUrl());
        values.put(RECIPE_INGREDIENTS, recipe.getIngredients());
        values.put(RECIPE_METHOD, recipe.getMethod());
        values.put(RECIPE_PREP_TIME, recipe.getPreparationTime());
        values.put(RECIPE_LAST_UPDATE, recipe.getLastUpdateDate());

        Log.d("TAG", "ResipeSQL:editRecipeDetails! ID: " + recipe.getId());
        db.update(RECIPE_TABLE, values, RECIPE_ID + "=" + recipe.getId(), null);
    }

    public static void deleteRecipe(SQLiteDatabase db, String recipeId){
        Log.d("TAG", "SQL : delete recipeID " + recipeId);
        db.delete(RECIPE_TABLE, RECIPE_ID + "=" + recipeId, null);
    }

    public static boolean checkIfIdAlreadyExists(SQLiteDatabase db, String recipeId) {
        Cursor cursor = db.query(RECIPE_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()){
            int recipeIdIndex = cursor.getColumnIndex(RECIPE_ID);
            do{
                if(cursor.getString(recipeIdIndex).equals(recipeId))
                        return true;
            }while (cursor.moveToNext());
        }
        return false;
    }

}

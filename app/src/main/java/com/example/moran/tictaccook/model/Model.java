package com.example.moran.tictaccook.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.moran.tictaccook.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by moran on 22/07/2017.
 */

public class Model {

    public static Model instance = new Model();

    ModelMem modelMem;
    ModelSql modelSql;
    ModelFirebase modelFirebase;

    private Model(){
        modelMem = new ModelMem();
        modelSql = new ModelSql(MyApplication.getContext());
        modelFirebase = new ModelFirebase();
    }


    public interface GetAllRecipesAndObserveCallback {
        void onComplete(List<Recipe> list);
        void onCancel();
    }

    /**
     * This function get all recipes by synchronize the local DB with Remote DB.
     * @param callBack
     */
    public void getAllRecipesAndObserve(final GetAllRecipesAndObserveCallback callBack) {
        //1. get local lastUpdateDate
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("RecipesSqlLastUpdateDate",0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        //2. get only updated records from FB
        modelFirebase.getAllRecipesAndObserve(lastUpdateDate, new ModelFirebase.GetAllRecipesAndObserveCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                double newLastUpdateDate = lastUpdateDate;
                Log.d("TAG", "FB fetch:" + list.size());
                for(Recipe recipe: list) {
                    if(!recipe.isArchive())
                    {
                        //3. update the local db
                        if (!RecipeSql.checkIfIdAlreadyExists(modelSql.getReadableDatabase(), recipe.getId())) {
                            RecipeSql.addRecipe(modelSql.getWritableDatabase(), recipe);
                        } else {
                            RecipeSql.editRecipeDetails(modelSql.getWritableDatabase(), recipe);
                        }
                        //4. update the local lastUpdateDate
                        if (newLastUpdateDate < recipe.getLastUpdateDate()) {
                            newLastUpdateDate = recipe.getLastUpdateDate();
                        }
                    }
                    else {
                        Log.d("TAG", "ARCHIVE: " + recipe.getId());
                        if (RecipeSql.checkIfIdAlreadyExists(modelSql.getReadableDatabase(), recipe.getId())) {
                            RecipeSql.deleteRecipe(modelSql.getWritableDatabase(), recipe.getId());
                        }
                    }
                }

                SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                        Context.MODE_PRIVATE).edit();
                prefEd.putFloat("RecipesSqlLastUpdateDate", (float) newLastUpdateDate);
                prefEd.commit();
                Log.d("TAG", "RecipesSqlLastUpdateDate NEW: " + newLastUpdateDate);

                //5. read from local db
                List<Recipe> data = RecipeSql.getAllRecipes(modelSql.getReadableDatabase());
                Log.d("TAG", "size SQL!!!!!!!!" + data.size());

                //6. return list of recipes
                callBack.onComplete(data);
            }

            @Override
            public void onCancel() {
                callBack.onCancel();
            }
        });

    }

    public void addRecipe(Recipe recipe) {
        modelFirebase.addRecipe(recipe);
    }

    public interface GetRecipeCallBack {
        void onComplete(Recipe recipe);
        void onCancel();
    }

    public void getRecipe(String recipeId, final GetRecipeCallBack callBack) {
        modelFirebase.getRecipe(recipeId, new ModelFirebase.GetRecipeCallBack() {
            @Override
            public void onComplete(Recipe recipe) {
                callBack.onComplete(recipe);
            }

            @Override
            public void onCancel() {
                callBack.onCancel();
            }
        });
    }

    public void editRecipeDetails(Recipe recipe){
        modelFirebase.editRecipeDetails(recipe);
    }

    public void deleteRecipe(Recipe recipe) {
        modelFirebase.deleteRecipe(recipe);
        RecipeSql.deleteRecipe(modelSql.getWritableDatabase(), recipe.getId());
    }

    public interface AlreadyExistsCallBack{
        void onComplete(boolean isExist);
        void onCancel();
    }

    public void checkIfIdAlreadyExists(String recipetId, final AlreadyExistsCallBack callBack) {
        modelFirebase.checkIfIdAlreadyExists(recipetId, new ModelFirebase.AlreadyExistsCallBack() {
            @Override
            public void onComplete(boolean isExist) {
                callBack.onComplete(isExist);
            }

            @Override
            public void onCancel() {
                callBack.onCancel();
            }
        });
    }

    /**
     * Save Image Listener
     */
    public interface SaveImageListener {
        void onComplete(String url);
        void onFailure();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener){
        Log.d("TAG", "Model : saveImage");
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void onComplete(String url) {
                Log.d("TAG", "Model : saveImage - onComplete");
                String fileName = URLUtil.guessFileName(url,null,null);
                ModelFiles.saveImageToFile(imageBmp,fileName);
                listener.onComplete(url);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Get Image Listener
     */
    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFailure();
    }

    public void getImage(final String url, final GetImageListener listener){
        // check if image exist locally
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsync(fileName, new ModelFiles.LoadImageFromFileAsync() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    Log.d("TAG", "getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                } else {
                    // if not, go to Firebase
                    modelFirebase.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG", "getImage from FB success " + fileName);
                            // save the image locally
                            ModelFiles.saveImageToFile(image, fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFailure() {
                            Log.d("TAG", "getImage from FB fail");
                            listener.onFailure();
                        }
                    });
                }
            }
        });

    }

    public void deleteImage(String url){
        modelFirebase.deleteImage(url);
    }


//    String initializeRecipeId(){
//        return "" + System.currentTimeMillis()/1000 + "_" + counter++;
//    }


    //--------------------------- USER ---------------------------

    public interface CreateAccountCallBack{
        void onComplete(String uId);
        void onError(String error);
    }

    public void createAccount(String email, String password, final CreateAccountCallBack callBack) {
        modelFirebase.createAccount(email, password, new ModelFirebase.CreateAccountCallBack() {
            @Override
            public void onComplete(String uId) {
                Log.d("TAG", "Model : createUserWithEmail:success");
                callBack.onComplete(uId);
            }

            @Override
            public void onError(String error) {
                Log.d("TAG", "Model : createUserWithEmail:failure");
                callBack.onError(error);
            }
        });
    }

    public interface SignInCallBack {
        void onComplete(String uId);
        void onError(String error);
    }

    public void signIn(String email, String password, final SignInCallBack callBack) {
        modelFirebase.signIn(email, password, new ModelFirebase.SignInCallBack() {
            @Override
            public void onComplete(String uId) {
                Log.d("TAG", "Model : signInWithEmail:success");
                callBack.onComplete(uId);
            }

            @Override
            public void onError(String error) {
                Log.d("TAG", "Model : signInWithEmail:failure");
                callBack.onError(error);
            }
        });
    }

    public String getCurrentUid(){
        return modelFirebase.getCurrentUid();
    }

    public void signOut(){
        modelFirebase.signOut();
    }

}



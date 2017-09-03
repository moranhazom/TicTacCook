package com.example.moran.tictaccook.model;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by moran on 04/08/2017.
 */

public class ModelFirebase {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    public ModelFirebase(){
        mAuth = FirebaseAuth.getInstance();
    }


    public void addRecipe(Recipe recipe){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        Map<String, Object> value = new HashMap<>();
        value.put("id", recipe.getId());
        value.put("name", recipe.getName());
        value.put("checked", recipe.isChecked());
        value.put("imageUrl", recipe.getImageUrl());
        value.put("ingredients", recipe.getIngredients());
        value.put("method", recipe.getMethod());
        value.put("preparationTime", recipe.getPreparationTime());
        value.put("lastUpdateDate", ServerValue.TIMESTAMP);
        value.put("archive", false);
        myRef.child(recipe.getId()).setValue(value);
    }

    /**
     * callBack for GetRecipe
     */
    interface GetRecipeCallBack {
        void onComplete(Recipe recipe);

        void onCancel();
    }

    public void getRecipe(String recipeId, final GetRecipeCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        myRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callBack.onComplete(recipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callBack.onCancel();
            }
        });
    }

    /**
     * callBack for get all recipes AndObserve
     */
    interface GetAllRecipesAndObserveCallback {
        void onComplete(List<Recipe> list);
        void onCancel();
    }

    public void getAllRecipesAndObserve(double lastUpdateDate, final GetAllRecipesAndObserveCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");

        //Get all records from Firebase with lastUpdateDate >  local lastUpdateDate
        myRef.orderByChild("lastUpdateDate").startAt(lastUpdateDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Recipe> list = new LinkedList<Recipe>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Recipe recipe = snap.getValue(Recipe.class);
                            list.add(recipe);
                        }
                        Log.d("TAG", "ModelFirebase : getAllRecipesAndObserve : return #list : " + list.size());
                        callback.onComplete(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onCancel();
                    }
                });
    }

    /**
     * callBack for Get All Recipes
     */
    interface GetAllRecipesCallback {
        void onComplete(List<Recipe> list);
        void onCancel();
    }

    public void GetAllRecipes(final GetAllRecipesCallback callback){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> list = new LinkedList<Recipe>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    list.add(recipe);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }


    /**
     * callBack for Already Exists CallBack
     */
    interface AlreadyExistsCallBack{
        void onComplete(boolean isExist);
        void onCancel();
    }
    public void checkIfIdAlreadyExists(String recipeId, final AlreadyExistsCallBack callBack){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        myRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    callBack.onComplete(true);
                }else {
                    callBack.onComplete(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callBack.onCancel();
            }
        });
    }

    public void deleteRecipe(Recipe recipe){
        Log.d("TAG", "ModalFirebase : deleteRecipe");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");

        Map<String, Object> value = new HashMap<>();
        value.put("id", recipe.getId());
        value.put("name", recipe.getName());
        value.put("checked", recipe.isChecked());
        value.put("imageUrl", recipe.getImageUrl());
        value.put("ingredients", recipe.getIngredients());
        value.put("method", recipe.getMethod());
        value.put("preparationTime", recipe.getPreparationTime());
        value.put("lastUpdateDate", ServerValue.TIMESTAMP);
        value.put("archive", true);
        myRef.child(recipe.getId()).setValue(value);

//        myRef.child(recipeId).removeValue();
    }

/**
 *     ToDo - if change id so the view goes out. need to use event bus
 *     ToDo - and need to check if id ! alreadyExist
 */
    public void editRecipeDetails(Recipe recipe){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipes");
        Map<String,Object> value = new HashMap<>();
        value.put("id",recipe.getId());
        value.put("name",recipe.getName());
        value.put("checked",recipe.isChecked());
        value.put("imageUrl",recipe.getImageUrl());
        value.put("ingredients",recipe.getIngredients());
        value.put("method",recipe.getMethod());
        value.put("preparationTime",recipe.getPreparationTime());
        value.put("lastUpdateDate", ServerValue.TIMESTAMP);

        myRef.child(recipe.getId()).setValue(value);

//        myRef.child(recipe.getId()).setValue(recipe);
    }

    /**
     * Upload image to Firebase
     * @param imageBmp
     * @param name
     * @param listener
     */
    public void saveImage(Bitmap imageBmp, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baot = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baot); //100 indicate highest quality
        byte[] data = baot.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG" , "ModelFireBase : saveImage - onSuccess");
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.onComplete(downloadUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }


    /**
     * download image from Firebase
     * @param url
     * @param listener
     */
    public void getImage(String url, final Model.GetImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);

        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("TAG", "Model Firebase : getImage - onSuccess");
                Bitmap imageBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                listener.onSuccess(imageBmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", e.getMessage());
                listener.onFailure();
            }
        });
    }

    /**
     * delete image from Firebase
     * @param url
     */
    public void deleteImage(String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        httpsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","ModelFirebase : deleteImage - onSuccess" );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","ModelFirebase : deleteImage - onFailure" );
            }
        });
    }



    //--------------------------- USER ---------------------------

    interface CreateAccountCallBack {
        void onComplete(String uId);
        void onError(String error);
    }

    public void createAccount(String email, String password, final CreateAccountCallBack callBack) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "ModelFirebase : createUserWithEmail:success");
                    currentUser = mAuth.getCurrentUser();
                    callBack.onComplete(task.getResult().getUser().getUid());
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.d("TAG", "ModelFirebase : createUserWithEmail:failure", task.getException());
                    callBack.onError(task.getException().getMessage().toString());
                }
            }
        });

    }

    interface SignInCallBack {
        void onComplete(String uId);
        void onError(String error);
    }

    public void signIn(String email, String password, final SignInCallBack callBack) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "ModelFirebase : signInWithEmail:success");
                    currentUser = mAuth.getCurrentUser();
                    callBack.onComplete(task.getResult().getUser().getUid());
                }
                else {
                    // If sign in fails, display a message to the user
                    Log.d("TAG", "ModelFireBase : signInWithEmail:failure", task.getException());
                    callBack.onError(task.getException().getMessage().toString());
                }
            }
        });

    }

    public String getCurrentUid(){
        return currentUser.getUid();
    }

    public void signOut() {
        mAuth.signOut();
    }

}

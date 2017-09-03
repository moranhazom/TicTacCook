package com.example.moran.tictaccook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import com.example.moran.tictaccook.model.Model;
import com.example.moran.tictaccook.model.Recipe;

public class MainActivity extends Activity
                            implements RecipesListFragment.OnFragmentInteractionListener,
                            RecipeDetailsFragment.OnFragmentInteractionListener,
                            RecipeEditFragment.OnFragmentInteractionListener,
                            NewRecipeFragment.OnFragmentInteractionListener{

    String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TAG","MainActivity: onCreate");
        RecipesListFragment recipesListFragment = RecipesListFragment.newInstance();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.main_container,recipesListFragment);
        tran.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.main_add:
                getActionBar().setDisplayHomeAsUpEnabled(true);
                NewRecipeFragment newRecipeFragment = NewRecipeFragment.newInstance();
                FragmentTransaction tranAdd = getFragmentManager().beginTransaction();
                tranAdd.replace(R.id.main_container,newRecipeFragment);
                tranAdd.addToBackStack("RecipesListFragment");//add current fragment to stack
                tranAdd.commit();
                break;

            case R.id.main_edit:
                Log.d("TAG","MainActivity:Edit Btn clicked");
                getActionBar().setDisplayHomeAsUpEnabled(true);
                RecipeEditFragment recipeEditFragment = RecipeEditFragment.newInstance(recipeID);
                FragmentTransaction tranEdit = getFragmentManager().beginTransaction();
                tranEdit.replace(R.id.main_container,recipeEditFragment);
                tranEdit.addToBackStack("RecipeDetailsFragment");
                tranEdit.commit();
                break;

            case R.id.main_signOut:
                Log.d("TAG","MainActivity:signOut Btn clicked");
                Model.instance.signOut();
                Intent intent = new Intent(MainActivity.this,AuthActivity.class);
                startActivity(intent);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    //override from RecipesListFragment.OnFragmentInteractionListener
    @Override
    public void onItemSelected(String itemID) {
        Log.d("TAG", "MainActivity: item selected: " + itemID);
        recipeID = itemID;
        RecipeDetailsFragment recipeDetailsFragment = RecipeDetailsFragment.newInstance(itemID);
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.main_container,recipeDetailsFragment);
        tran.addToBackStack("RecipesListFragment");
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //override from RecipeDetailsFragment.OnFragmentInteractionListener
    @Override
    public void onUpdateRecipeId(String recipeId) {
        Log.d("TAG", "MainActivity:onUpdateRecipeId: " + recipeId);
        recipeID = recipeId;
    }

    //override from RecipeEditFragment.OnFragmentInteractionListener
    @Override
    public void onCancelEdit() {
        Log.d("TAG", "MainActivity:Cancel Btn was clicked");
        getFragmentManager().popBackStack();
    }

    //override from RecipeEditFragment.OnFragmentInteractionListener
    @Override
    public void onDeleteEdit() {
        Log.d("TAG", "MainActivity:Delete Btn was clicked");
//        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setMessage("Deleted successfully")
//                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getFragmentManager().popBackStack();
//                        getFragmentManager().popBackStack();
//                    }
//                }).show();
        getFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
//        getFragmentManager().popBackStack();
//        getFragmentManager().popBackStack();
    }

    //override from RecipeEditFragment.OnFragmentInteractionListener
    @Override
    public void onSaveEdit() {
        Log.d("TAG", "MainActivity:Save Btn was clicked");
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Saved successfully")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).show();
    }

    //TODO - fix no true false
    //override from NewRecipeFragment.OnFragmentInteractionListener
    @Override
    public void onSaveNewRecipe(boolean idValidation) {
        Log.d("TAG", "MainActivity:Save Btn was clicked");
        if (idValidation) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Saved successfully")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                        }
                    }).show();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage("Recipe ID already exists")
                    .setNeutralButton("OK", null).show();
        }
    }

    //override from NewRecipeFragment.OnFragmentInteractionListener
    @Override
    public void onCancelNewRecipe() {
        Log.d("TAG", "MainActivity:Cancel Btn was clicked");
        getFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getFragmentManager().popBackStack();
        }
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}

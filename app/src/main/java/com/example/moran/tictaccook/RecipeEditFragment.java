package com.example.moran.tictaccook;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moran.tictaccook.model.Model;
import com.example.moran.tictaccook.model.Recipe;

import static android.app.Activity.RESULT_OK;


public class RecipeEditFragment extends Fragment {

    private static final String ARG_PARAM1 = "itemID";
    private String itemID;

    private OnFragmentInteractionListener mListener;

    Recipe recipe;
    ImageView imageView;
    Bitmap imageBitmap;
    boolean isEditImage = false;

    public RecipeEditFragment() {
    }

    public static RecipeEditFragment newInstance(String itemID) {
        RecipeEditFragment fragment = new RecipeEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, itemID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            itemID = getArguments().getString(ARG_PARAM1);

            Log.d("TAG", "RecipeEditFragment:onCreate. got from ARG_PARAM1 = " + itemID);

//            reciprRowId = Model.instance.getRowID(itemID);
//            recipe = Model.instance.getRecipe(itemID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "RecipeEditFragment:onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_recipe_edit, container, false);
        //recipe = Model.instance.getRecipe(reciprRowId);

        final EditText nameEt = (EditText) contentView.findViewById(R.id.recipeEdit_name);
        final EditText idEt = (EditText) contentView.findViewById(R.id.recipeEdit_id);
        final EditText prepTimeEt = (EditText) contentView.findViewById(R.id.recipeEdit_time);
        final EditText ingEt = (EditText) contentView.findViewById(R.id.recipeEdit_ing);
        final EditText methodEt = (EditText) contentView.findViewById(R.id.recipeEdit_method);
        final CheckBox cb = (CheckBox) contentView.findViewById(R.id.recipeEdit_cb);

        imageView = (ImageView) contentView.findViewById(R.id.recipeEdit_image);

        Button cancelBtn = (Button) contentView.findViewById(R.id.recipeEdit_cancelBtn);
        Button deleteBtn = (Button) contentView.findViewById(R.id.recipeEdit_deleteBtn);
        Button saveBtn = (Button) contentView.findViewById(R.id.recipeEdit_saveBtn);

        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.recipeEdit_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Model.instance.getRecipe(itemID, new Model.GetRecipeCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(Recipe recipe) {
                Log.d("TAG", "RecipeEditFragment:onCreateView - getRecipe success");
                progressBar.setVisibility(View.GONE);

                RecipeEditFragment.this.recipe = recipe;

                nameEt.setText(RecipeEditFragment.this.recipe.getName());
                idEt.setText(RecipeEditFragment.this.recipe.getId());
                prepTimeEt.setText(RecipeEditFragment.this.recipe.getPreparationTime());
                ingEt.setText(RecipeEditFragment.this.recipe.getIngredients());
                methodEt.setText(RecipeEditFragment.this.recipe.getMethod());
                cb.setChecked(RecipeEditFragment.this.recipe.isChecked());


                imageView.setImageDrawable(getActivity().getDrawable(R.drawable.food));

                //TODO - progress bar
                //final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.recipeRow_progressBar);

                if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty() && !recipe.getImageUrl().equals("")) {
                    //progressBar.setVisibility(View.VISIBLE);
                    Model.instance.getImage(recipe.getImageUrl(), new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            Log.d("TAG", "RecipeEditFragment:onCreateView - getImage success");
                            imageBitmap = image;
                            imageView.setImageBitmap(image);
                            //progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure() {
                            //progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "RecipeDetailsFragment:onCreate - getRecipe - onCancel");
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "RecipeEditFragment:cancelBtn clicked");
                mListener.onCancelEdit();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "RecipeEditFragment:deleteBtn clicked");
                mListener.onDeleteEdit();
                Model.instance.deleteRecipe(recipe);
            }
        });

        //TODO - need this!! just need to fix!!!!

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "RecipeEditFragment:saveBtn clicked");
                recipe.setName(nameEt.getText().toString());
                recipe.setId(idEt.getText().toString());
                recipe.setPreparationTime(prepTimeEt.getText().toString());
                recipe.setIngredients(ingEt.getText().toString());
                recipe.setMethod(methodEt.getText().toString());
                recipe.setChecked(cb.isChecked());


                if(isEditImage){ // && imageBitmap != null
                    long timestamp = System.currentTimeMillis()/1000;
                    //delete the old image from Db - in func onActivity Result - need to be here
                    Model.instance.saveImage(imageBitmap, ("" + timestamp + recipe.getId()).toString() + ".jpeg", new Model.SaveImageListener() {
                        @Override
                        public void onComplete(String url) {
                            Log.d("TAG", "RecipeEditFragment : saveImage - onComplete");
                            recipe.setImageUrl(url);
                            Model.instance.editRecipeDetails(recipe);
                            mListener.onSaveEdit();
                        }

                        @Override
                        public void onFailure() {
                            Log.d("TAG", "RecipeEditFragment : saveImage - onFailure");
                        }
                    });
                }
                else{
                    // Otherwise save Recipe details without the image
                    Model.instance.editRecipeDetails(recipe);
                    mListener.onSaveEdit();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        return contentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem editMenuItem = menu.findItem(R.id.main_edit);
        MenuItem addMenuItem = menu.findItem(R.id.main_add);
        editMenuItem.setVisible(false);
        addMenuItem.setVisible(false);
    }

    /**
     * This interface implemented by MainActivity that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onCancelEdit();
        void onDeleteEdit();
        void onSaveEdit();
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Take Picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("TAG","RecipeEditFragment : onActivityResult - resultCode = RESULT_OK");
            isEditImage = true;
            // delete the prev Image if exist
            if(recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty() && !recipe.getImageUrl().equals("")){
                Model.instance.deleteImage(recipe.getImageUrl());
            }
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
        else{
            Log.d("TAG","RecipeEditFragment : onActivityResult - resultCode != RESULT_OK");
        }
    }
}

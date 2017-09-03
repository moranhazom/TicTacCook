package com.example.moran.tictaccook;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.moran.tictaccook.model.Model;
import com.example.moran.tictaccook.model.Recipe;

import static android.app.Activity.RESULT_OK;


public class NewRecipeFragment extends Fragment {

    ImageView imageView;
    Bitmap imageBitmap;

    private OnFragmentInteractionListener mListener;

    public NewRecipeFragment() {
    }

    public static NewRecipeFragment newInstance() {
        NewRecipeFragment fragment = new NewRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//call to preper
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "NewRecipeFragment:onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_new_recipe, container, false);

        final EditText nameEt = (EditText) contentView.findViewById(R.id.newRecipe_name);
        final EditText idEt = (EditText) contentView.findViewById(R.id.newRecipe_id);
        final EditText prepTimeEt = (EditText) contentView.findViewById(R.id.newRecipe_time);
        final EditText ingEt = (EditText) contentView.findViewById(R.id.newRecipe_ing);
        final EditText methodEt = (EditText) contentView.findViewById(R.id.newRecipe_method);
        final CheckBox cb = (CheckBox) contentView.findViewById(R.id.newRecipe_cb);

        imageView = (ImageView) contentView.findViewById(R.id.newRecipe_image);

        Button cancelBtn = (Button) contentView.findViewById(R.id.newRecipe_cancelBtn);
        Button saveBtn = (Button) contentView.findViewById(R.id.newRecipe_saveBtn);

        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.newRecipe_progressBar);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","NewRecipeFragment:Save Btn was clicked");
                progressBar.setVisibility(View.VISIBLE);

                /**
                 * Check if the new recipeID already exists in DB.
                 * If not, allow the new student to be created and return true to the listener.
                 */

                Model.instance.checkIfIdAlreadyExists(idEt.getText().toString(), new Model.AlreadyExistsCallBack() {
                    @Override
                    public void onComplete(boolean isExist) {
                        Log.d("TAG", "NewRecipeFragment - checkIfIdAlreadyExists - onComplete success");
                        if (!isExist) {
                            Log.d("TAG", "NewRecipeFragment - checkIfIdAlreadyExists - recipeID: " + idEt.getText().toString() + "Not Exist");
                            final Recipe recipe = new Recipe();
                            recipe.setName(nameEt.getText().toString());
                            recipe.setId(idEt.getText().toString());
                            recipe.setImageUrl("");
                            recipe.setPreparationTime(prepTimeEt.getText().toString());
                            recipe.setIngredients(ingEt.getText().toString());
                            recipe.setMethod(methodEt.getText().toString());
                            recipe.setChecked(cb.isChecked());

                            if(imageBitmap != null){
                                long timestamp = System.currentTimeMillis()/1000;
                                Model.instance.saveImage(imageBitmap, ("" + timestamp + recipe.getId()).toString() + ".jpeg", new Model.SaveImageListener() {
                                    @Override
                                    public void onComplete(String url) {
                                        Log.d("TAG", "NewRecipeFragment : saveImage - onComplete");
                                        progressBar.setVisibility(View.GONE);
                                        recipe.setImageUrl(url);
                                        Model.instance.addRecipe(recipe);
                                        mListener.onSaveNewRecipe(true);
                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                            }
                            else{
                                // Otherwise save Recipe details without the image
                                progressBar.setVisibility(View.GONE);
                                Model.instance.addRecipe(recipe);
                                mListener.onSaveNewRecipe(true);
                            }

                        } else {
                            //Otherwise, return false to the listener to display a message
                            Log.d("TAG", "NewRecipeFragment - checkIfIdAlreadyExists - recipeID: " + idEt.getText().toString() + "Already Exist!");
                            mListener.onSaveNewRecipe(false);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG", "NewRecipeFragment - checkIfIdAlreadyExists - onCancel");
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","NewRecipeFragment:Cancel Btn was clicked");
                mListener.onCancelNewRecipe();

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
        void onSaveNewRecipe(boolean idValidation);
        void onCancelNewRecipe();
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
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}

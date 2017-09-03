package com.example.moran.tictaccook;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moran.tictaccook.model.Model;
import com.example.moran.tictaccook.model.Recipe;


public class RecipeDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "itemID";
    private String itemID;

    private OnFragmentInteractionListener mListener;

    private Recipe recipe;
    TextView nameTv;
    TextView idTv;
    TextView prepTimeTv;
    TextView ingTv;
    TextView methodTv;
    CheckBox cb;
    ImageView imageView;

    public RecipeDetailsFragment() {
    }

    public static RecipeDetailsFragment newInstance(String itemID) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, itemID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//call to preper
        if (getArguments() != null) {
            itemID = getArguments().getString(ARG_PARAM1);
            Log.d("TAG", "RecipeDetailsFragment:onCreate itemId = " + itemID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "RecipeDetailsFragment:onCreateView");
        View contentView = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        nameTv = (TextView) contentView.findViewById(R.id.recipeDetails_name);
        idTv = (TextView) contentView.findViewById(R.id.recipeDetails_id);
        prepTimeTv = (TextView) contentView.findViewById(R.id.recipeDetails_time);
        ingTv = (TextView) contentView.findViewById(R.id.recipeDetails_ing);
        methodTv = (TextView) contentView.findViewById(R.id.recipeDetails_method);
        cb = (CheckBox) contentView.findViewById(R.id.recipeDetails_cb);

        imageView = (ImageView) contentView.findViewById(R.id.recipeDetails_image);

        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.recipeDetails_progressBar);
        final ProgressBar progressBarImg = (ProgressBar) contentView.findViewById(R.id.recipeDetails_progressBarImg);
        progressBar.setVisibility(View.VISIBLE);
        progressBarImg.setVisibility(View.VISIBLE);

        Model.instance.getRecipe(itemID, new Model.GetRecipeCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(Recipe recipe) {
                Log.d("TAG", "RecipeDetailsFragment:onCreate - getRecipe success - recipeId: " + recipe.getId());
                progressBar.setVisibility(View.GONE);
                RecipeDetailsFragment.this.recipe = recipe;

                nameTv.setText(RecipeDetailsFragment.this.recipe.getName());
                idTv.setText(RecipeDetailsFragment.this.recipe.getId());
                prepTimeTv.setText(RecipeDetailsFragment.this.recipe.getPreparationTime());
                ingTv.setText(RecipeDetailsFragment.this.recipe.getIngredients());
                methodTv.setText(RecipeDetailsFragment.this.recipe.getMethod());
                cb.setChecked(RecipeDetailsFragment.this.recipe.isChecked());

                imageView.setImageDrawable(getActivity().getDrawable(R.drawable.food));

                if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty() && !recipe.getImageUrl().equals("")) {
                    progressBarImg.setVisibility(View.VISIBLE);
                    Model.instance.getImage(recipe.getImageUrl(), new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            Log.d("TAG", "RecipeEditFragment:onCreateView - getImage success");
                            //imageBitmap = image;
                            imageView.setImageBitmap(image);
                            progressBarImg.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure() {
                            progressBarImg.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "RecipeDetailsFragment:onCreate - getRecipe - onCancel");
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
    public void onPause() {
        Log.d("TAG", "RecipeDetailsFragment:onPause");
        super.onPause();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem editMenuItem = menu.findItem(R.id.main_edit);
        MenuItem addMenuItem = menu.findItem(R.id.main_add);
        editMenuItem.setVisible(true);
        addMenuItem.setVisible(false);
    }

    /**
     * This interface implemented by MainActivity that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onUpdateRecipeId(String recipeId);
    }
}

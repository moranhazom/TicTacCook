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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moran.tictaccook.model.Model;
import com.example.moran.tictaccook.model.Recipe;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesListFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView list;
    List<Recipe> data = new LinkedList<>();
    RecipeListAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public RecipesListFragment() {
    }

    public static RecipesListFragment newInstance() {
        RecipesListFragment fragment = new RecipesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","RecipesListFragment:onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG","RecipesListFragment:onCreateView");
        final View contentView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.recipesList_progressBar);

        RecipesListFragment.this.list = (ListView) contentView.findViewById(R.id.recipesList_list);
        adapter = new RecipeListAdapter();
        RecipesListFragment.this.list.setAdapter(adapter);
        RecipesListFragment.this.list.setOnItemClickListener(RecipesListFragment.this);

        progressBar.setVisibility(View.VISIBLE);

        Model.instance.getAllRecipesAndObserve(new Model.GetAllRecipesAndObserveCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                Log.d("TAG", "RecipesListFragment:onCreateView - getAllRecipesAndObserve success");
                progressBar.setVisibility(View.GONE);
                data = list;
                adapter.notifyDataSetChanged();

                Log.d("TAG","RecipesListFragment:onCreateView: #data = " + data.size());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "RecipesListFragment:onCreateView - getAllRecipesAndObserve - onCancel");
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

    //this class implements AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemSelected(data.get(position).getId());
    }

    /**
     * This interface implemented by MainActivity that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onItemSelected(String itemID);
    }


    class RecipeListAdapter extends BaseAdapter{

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        class CBListener implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                int pos = (int)v.getTag();
                Recipe recipe = data.get(pos);
                recipe.setChecked(!recipe.isChecked());
            }
        }

        CBListener listener = new  CBListener();

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.recipe_list_row,null);
                CheckBox cb = (CheckBox) convertView.findViewById(R.id.recipeRow_cb);
                cb.setOnClickListener(listener);
            }

            TextView nameTV = (TextView) convertView.findViewById(R.id.recipeRow_name);
            TextView timePrepTv = (TextView) convertView.findViewById(R.id.recipeRow_time);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.recipeRow_image);
            CheckBox cb = (CheckBox) convertView.findViewById(R.id.recipeRow_cb);

            final Recipe recipe = data.get(position);
            nameTV.setText(recipe.getName());
            timePrepTv.setText(recipe.getPreparationTime());
            cb.setChecked(recipe.isChecked()); //when cb goes out from the display- the cb cleared
            cb.setTag(position);

            imageView.setTag(recipe.getImageUrl());

            imageView.setImageDrawable(getActivity().getDrawable(R.drawable.food));
//            imageView.setImageResource(R.drawable.food);

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.recipeRow_progressBar);

            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty() && !recipe.getImageUrl().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(recipe.getImageUrl(), new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String tagUrl = imageView.getTag().toString();
                        if (tagUrl.equals(recipe.getImageUrl())) {
                            Log.d("TAG", "RecipesListFragment:RecipeListAdapter - getImage success");
                            imageView.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            return convertView;
        }
    }
}

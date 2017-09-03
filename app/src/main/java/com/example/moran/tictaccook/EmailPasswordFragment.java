package com.example.moran.tictaccook;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.moran.tictaccook.model.Model;


public class EmailPasswordFragment extends Fragment {

    EditText emailEt;
    EditText passwordEt;

    private OnFragmentInteractionListener mListener;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }

    public static EmailPasswordFragment newInstance() {
        EmailPasswordFragment fragment = new EmailPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_email_password, container, false);

        emailEt = (EditText) contentView.findViewById(R.id.emailPass_email);
        passwordEt = (EditText) contentView.findViewById(R.id.emailPass_password);
        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.emailPass_progressBar);

        Button registerBtn = (Button) contentView.findViewById(R.id.emailPass_registerBtn);
        Button signInBtn = (Button) contentView.findViewById(R.id.emailPass_signInBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFormCreateAccount()) {
                    progressBar.setVisibility(View.VISIBLE);

                    Model.instance.createAccount(emailEt.getText().toString(), passwordEt.getText().toString(), new Model.CreateAccountCallBack() {
                        @Override
                        public void onComplete(String uid) {
                            Log.d("TAG", "EmailpassFragment : createAccount:success uId: " + uid);
                            mListener.onRegisterSuccess(uid);
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("TAG", "EmailpassFragment : createAccount:failure");
                            progressBar.setVisibility(View.GONE);
                            mListener.onRegisterFailed(error);
                        }
                    });
                }
            }
        });


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFormSignIn()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Model.instance.signIn(emailEt.getText().toString(), passwordEt.getText().toString(), new Model.SignInCallBack() {
                        @Override
                        public void onComplete(String uId) {
                            Log.d("TAG", "EmailpassFragment : SignIn:success uId: " + uId);
                            mListener.onSignInSuccess(uId);
                        }

                        @Override
                        public void onError(String error) {
                            Log.d("TAG", "EmailpassFragment : SignIn:failure");
                            progressBar.setVisibility(View.GONE);
                            mListener.onSignInFailed(error);
                        }
                    });

                }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener)activity;
        } else {

            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onRegisterSuccess(String uId);
        void onSignInSuccess(String uId);
        void onRegisterFailed(String error);
        void onSignInFailed(String error);
    }

    /**
     * validation for registration and signIn form
     * @return
     */
    boolean validateFormCreateAccount() {
        boolean valid = true;

        String email = emailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Required.");
            valid = false;
        }
        else if(!email.contains("@") || !email.contains(".")){
            emailEt.setError("Email is not valid.");
            valid = false;
        }

        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            passwordEt.setError("Password must be at least 6 characters.");
            valid = false;
        }

        return valid;
    }

    boolean validateFormSignIn() {
        boolean valid = true;

        String email = emailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Required.");
            valid = false;
        }

        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Required.");
            valid = false;
        }

        return valid;
    }
}

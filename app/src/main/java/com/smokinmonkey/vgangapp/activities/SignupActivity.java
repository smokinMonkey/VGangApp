package com.smokinmonkey.vgangapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.smokinmonkey.vgangapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {

    // debug tag
    private static final String TAG = "SingupActivity";
    // Firebase Auth objects
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    // butter knife bind
    @BindView(R.id.et_signup_email)
    EditText mSignupEmail;
    @BindView(R.id.et_signup_name)
    EditText mSignupName;
    @BindView(R.id.et_signup_password)
    EditText mSignupPassword;
    @BindView(R.id.et_signup_confirm_password)
    EditText mSignupConfirmPassword;
    @BindView(R.id.btn_signup)
    Button mSignupBtn;
    @BindView(R.id.tv_link_login)
    TextView mLoginLink;

    // set onClick for signup
    @OnClick(R.id.btn_signup)
    public void signup() {
        final String name = mSignupName.getText().toString().trim();
        String email = mSignupEmail.getText().toString().trim();
        String password = mSignupPassword.getText().toString().trim();
        String confirmPassword = mSignupConfirmPassword.getText().toString().trim();

        // checks if information enterred is valid
        if(!validate(name, email, password, confirmPassword)) {
            onSignupFailed();
            return;
        }

        // disable sign up button
        mSignupBtn.setEnabled(false);

        final ProgressDialog pd = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Dialog);
        pd.setIndeterminate(true);
        pd.setMessage("Creating account...");
        pd.show();

        // Firebase signup new users with email password
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // signup success, update UI and Firebase user
                            Log.d(TAG, "Firebase create user with email and password: SUCCESS");
                            mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            mFirebaseUser.updateProfile(profileUpdates).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Log.d(TAG, "IMPORANT********* " + mFirebaseUser.getDisplayName()
                                                        .toString());
                                            }
                                        }
                                    });
                        } else {
                            // signup failed, display message to user
                            Log.d(TAG, "Firebase create user with email and password: FAILED",
                                    task.getException());
                            Toast.makeText(SignupActivity.this, "Signup FAILED...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mSignupBtn.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    // set onClick for login link
    @OnClick(R.id.tv_link_login)
    public void loginLink() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // bind butter knife with this activity
        ButterKnife.bind(this);

        // initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "signup failed...", Toast.LENGTH_LONG).show();
        mSignupBtn.setEnabled(true);
    }

    private boolean validate(String name, String email, String password, String confirmPassword) {
        // check if name is not empty or more than 3 characters
        if(name.isEmpty() || name.length() < 3) {
            mSignupName.setError("please enter at least 3 characters.");
            return false;
        } else {
            mSignupName.setError(null);
        }
        // check if email is valid email
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mSignupEmail.setError("please enter a valid email address.");
            return false;
        } else {
            mSignupEmail.setError(null);
        }
        // check if password is between 4 - 10 alphanumeric characters
        if(password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mSignupPassword.setError("please keep password between 4 and 10 alphanumeric characters.");
            return false;
        } else {
            mSignupPassword.setError(null);
        }
        // check if password and confirm password match
        if(confirmPassword.isEmpty() || !password.matches(confirmPassword)) {
            mSignupConfirmPassword.setError("password and confirm password do not match.");
            return false;
        } else {
            mSignupConfirmPassword.setError(null);
        }

        return true;
    }

}

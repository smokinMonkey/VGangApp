package com.smokinmonkey.vgangapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.smokinmonkey.vgangapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    // debug tag
    private static final String TAG = "LoginActivity";
    // request signup
    private static final int REQUEST_SIGNUP = 1;
    // google sign in
    private static final int RC_SIGN_IN = 1001;
    // Firebase Auth objects
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    // Google sign in objects
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignIn mGoogleSignin;
    // facebook sign in objects
    private CallbackManager mCallbackManager;
    private FacebookCallback mFacebookCallback;

    // butter knife bind views
    @BindView(R.id.et_email)
    EditText mEmail;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.tv_link_signup)
    TextView mSingupLink;
    @BindView(R.id.btn_google_login)
    SignInButton mGoogleLoginBtn;
//    @BindView(R.id.btn_facebook_login)
//    ImageView mFacebookLoginBtn;

    // set onClick listener for login button
    @OnClick(R.id.btn_login)
    public void login() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        // checks if email and password is empty or valid
        if(!validate(email, password)) {
            // login failed
            onLoginFailed();
            return;
        }

        // pre login validates, login with email and password
        login(email, password);
    }

    // set onClick for google login
    @OnClick(R.id.btn_google_login)
    public void googleLogin() {
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    // set onClick for singup link
    @OnClick(R.id.tv_link_signup)
    public void signUp() {
        // start signup activity
        Intent i = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(i, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google SignIn result: failed... fail code = " + e.getStatusCode());

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());
        //showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // sign in success, update UI with the signed in user's information
                            Log.d(TAG, "signInWithCredential: success");
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();

                            // update user UI
                            //updateUI(mFirebaseUser);
                        } else {
                            // google sign-in fails, display message to user
                            Log.w(TAG, "signInWithCredential: failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Google Sign In failed.",
                                    Toast.LENGTH_SHORT).show();

                            //updateUI(null);
                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // bind butter knife with this activity
        ButterKnife.bind(this);
        // initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // google signin options
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        /*
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
        */
    }

    private void login(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // sign in success
                            Toast.makeText(LoginActivity.this, "Authentication SUCCESS!",
                                    Toast.LENGTH_SHORT).show();
                            mFirebaseUser = mFirebaseAuth.getInstance().getCurrentUser();

                            // user logged in and start home activity

                        } else {
                            // sing in fails, display message to user
                            Toast.makeText(LoginActivity.this, "Authentication FAILED!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onLoginFailed() {
        Toast.makeText(LoginActivity.this, "Email and Password do not match...",
                Toast.LENGTH_LONG).show();
    }

    private boolean validate(String email, String password) {
        // checks if email is valid email address
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter account email.");
            return false;
        } else {
            mEmail.setError(null);
        }

        // check if password is between 4-10 alphanumeric characters
        if(password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPassword.setError("Please enter account password.");
            return false;
        } else {
            mPassword.setError(null);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

package io.github.entertainmatch.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.model.Person;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.view.dialog.InternetDialogFragment;

public class LoginActivity extends AppCompatActivity implements InternetDialogFragment.OnFragmentInteractionListener {
    private final static String Tag = "EntertainMatch_Login";

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.loginFb)
    LoginButton loginButton;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookInitializer.init(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("MAIN", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("MAIN", "onAuthStateChanged:signed_out");
            }
            // ...
        };
        
        checkNetworkConnection();

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    private void init() {
        if (FacebookUsers.isUserLoggedIn(this)) {
            goToApp();
        }
        // else - stay in login activity
    }

    private void checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            // not connected to the internet
            DialogFragment fragment = InternetDialogFragment.newInstance();
            fragment.show(getFragmentManager(), "tag");
        } else {
            // connected to the internet, just start the app
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("MAIN", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                Log.d("MAIN", "signInWithCredential:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.w("MAIN", "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void goToApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupLoginButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // quit immediately without re-rendering
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() &&
                resultCode == Activity.RESULT_OK)
            finish();
    }

    private void setupLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile", "user_friends");

        setupLoginCallback();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            performFacebookLogin();
            }
        });
    }

    private void setupLoginCallback() {
        loginCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                        try {
                            Person user = new Person(response.getJSONObject());
                            FacebookUsers.setCurrentUser(LoginActivity.this, user);
                            // note in Firebase that user has been created successfully.
                            FirebaseUserController.addPerson(user);

                            // just for now to see if everything works
                            Toast.makeText(LoginActivity.this, "Welcome aboard, " + user.getName(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        goToApp();
                        }

                    });

                Bundle parameters = new Bundle();
                parameters.putString("fields", getString(R.string.requested_facebook_info));
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(Tag, "Facebook login error -- " + e.getMessage());
            }
        };
    }

    private void performFacebookLogin() {
        loginButton.performClick();
        loginButton.registerCallback(callbackManager, loginCallback);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment) {
        // check again
        checkNetworkConnection();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment fragment) {
        finish();
    }
}

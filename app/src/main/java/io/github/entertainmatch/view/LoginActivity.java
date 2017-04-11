package io.github.entertainmatch.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.facebook.model.FacebookUser;

public class LoginActivity extends AppCompatActivity {
    private final static String Tag = "EntertainMatch_Login";

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.loginFb)
    LoginButton loginButton;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();

        if (FacebookUsers.isUserLoggedIn(this)) {
            goToApp();
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
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
                GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                        try {
                            FacebookUser user = FacebookUser.fromJSON(object);
                            FacebookUsers.setCurrentUser(LoginActivity.this, user);

                            // just for now to see if everything works
                            Toast.makeText(LoginActivity.this, "Welcome aboard -- " + user.name, Toast.LENGTH_LONG).show();
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

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}

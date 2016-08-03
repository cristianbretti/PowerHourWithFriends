package cristianosoriobretti.powerhourwithfriends;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;

public class LoginActivity extends AppCompatActivity implements ConnectionStateCallback {

    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";
    private static final String REDIRECT_URI = "power-hour-with-friends-login://callback";

    private String token;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_first);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = AuthenticationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d("App", "TOKEN");
                    token = response.getAccessToken();
                    JsonHandler handler = new JsonHandler();
                    user = handler.createUser(token);
                    //TODO: create intent to loginActivity
                    Intent showListIntent = new Intent(LoginActivity.this, PlaylistActivity.class);
                    showListIntent.putExtra("user", user);
                    startActivity(showListIntent);
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("App", "ERROR");
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.d("App", "default");
            }
        }
        else {
            if(intent != null){
                int logoutFlag = intent.getIntExtra("f", 0);
                if(logoutFlag == 1){
                    createLogoutBrowser();
                }
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
        System.out.println("Kom hir");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }


    public void loginBtnClicked(View v){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-read-private"});
        AuthenticationRequest request = builder.build();


        AuthenticationClient.openLoginInBrowser(this, request);
    }

    private void createLogoutBrowser() {
        String urlString="https://accounts.spotify.com";
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            this.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            this.startActivity(intent);
        }
    }
}

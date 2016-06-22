package cristianosoriobretti.powerhourwithfriends;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LoginActivity extends Activity implements ConnectionStateCallback {


    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";
    private static final String REDIRECT_URI = "power-hour-with-friends-login://callback";

    private TextView textViewUser;
    private String token;
    private User user;
    private LinearLayout layoutPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        layoutPlaylist = (LinearLayout) findViewById(R.id.layoutPlaylist);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginInBrowser(this, request);


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
                    textViewUser.setText(user.getUserName());
                    populateSrollView();
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

    private void populateSrollView(){
        ArrayList<Playlist> list = user.getListOfPlaylists();
        for(int i = 0; i < list.size(); i++){
            String nameOfPlaylist = list.get(i).getName();
            final TextView view = new TextView(this);
            view.setId(i);
            view.setText(nameOfPlaylist);
            //Set the correct layout
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0,22,0,22);
            view.setLayoutParams(llp);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, PlayActivity.class);

                    int id = view.getId();
                    intent.putExtra("user", user);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            layoutPlaylist.addView(view);

        }
    }
}
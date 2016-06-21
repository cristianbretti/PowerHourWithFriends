package cristianosoriobretti.powerhourwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class PlayActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback{

    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";

    private Player mPlayer;
    private User user;
    private Playlist playlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        TextView textView = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        int i = intent.getIntExtra("id", 0);
        playlist = user.getListOfPlaylists().get(i);
        playMusic();

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }

    private void playMusic(){
        Config playerConfig = new Config(this, user.getAuthCode(),CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer = player;
                mPlayer.addConnectionStateCallback(PlayActivity.this);
                mPlayer.addPlayerNotificationCallback(PlayActivity.this);
                mPlayer.play(playlist.getList().get(0).getUri());
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    protected void onDestroy(){
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}

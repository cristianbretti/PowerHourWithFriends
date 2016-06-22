package cristianosoriobretti.powerhourwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class PlayActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback, Runnable{

    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";
    private Player mPlayer;
    private User user;
    private Playlist playlist;
    SleepRunnable sleep;
    Timer timer;

    long timeLeftOfSong;
    long startTime;
    int timesStarted;
    final int standartTime = 60000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        timeLeftOfSong = standartTime; //Time in milliseconds
        timesStarted = 0;

        TextView textView = (TextView) findViewById(R.id.textView);
        sleep = new SleepRunnable();
        timer = new Timer();
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
        Config playerConfig = new Config(this, user.getAuthCode(), CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

            @Override
            public void onInitialized(Player player) {
                mPlayer = player;
                mPlayer.addConnectionStateCallback(PlayActivity.this);
                mPlayer.addPlayerNotificationCallback(PlayActivity.this);
                mPlayer.play(playlist.getList().get(0).getUri());
                for (Track current : playlist.getList()){
                    mPlayer.queue(current.getUri());
                }
                Log.d("Player", "Playing for first time");
                startTime = System.currentTimeMillis();
                timesStarted ++;
                Log.d("Start time now", "" + startTime);

                // sleep.start();
                run();
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
        Log.d("Player", "player destroyed");
    }

    public void pauseClick (View view){
        long elapsedTime = System.currentTimeMillis() - startTime;
        timeLeftOfSong -= elapsedTime;
        mPlayer.pause();
        timesStarted++;
        Log.d("PAUSE", "Elapsed time before pause: " + elapsedTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
    }

    public void playClick (View view){
        mPlayer.resume();
        startTime = System.currentTimeMillis();
        Log.d("PLAY", "Start time: " + startTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
        run();
    }

    public void nextSong(int check){
        if (check == timesStarted){
            mPlayer.skipToNext();
            timeLeftOfSong = standartTime;
            timesStarted = 1;
            startTime = System.currentTimeMillis();
            Log.d("NEXT SONG", "Start time: " + startTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
            run();
        }
    }

    @Override
    public void run() {
        final int hold = timesStarted;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                nextSong(hold);
                Log.d("Thread", "Done with run, times started: " + hold);
            }
        }, timeLeftOfSong);
    }
}
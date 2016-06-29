package cristianosoriobretti.powerhourwithfriends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class PlayActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback{

    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";
    private Player mPlayer;
    private User user;
    private Playlist playlist;
    private ArrayList<String> listOfSongNamesAndArtist;

    TextView textView;
    TextView songsLeftText;
    TextView countDownText;

    CountDownTimer countdown;
    Timer timer;
    long timeLeftOfSong;
    long startTime;
    boolean paused;
    boolean betweenSongs;

    Vibrator vib;

    String currentSong;
    int songNumber;

    int numberOfSongs = 60;
    final int standartTime = 20000;
    PopupWindow pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_play);
        timer = new Timer();
        vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        listOfSongNamesAndArtist = new ArrayList<>();

        timeLeftOfSong = standartTime; //Time in millisecond

        textView = (TextView) findViewById(R.id.textView);
        songsLeftText = (TextView) findViewById(R.id.songLeftText);
        countDownText = (TextView) findViewById(R.id.countDownText);

        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        int i = intent.getIntExtra("id", 0);
        playlist = user.getListOfPlaylists().get(i);


        findViewById(R.id.play_activity).post(new Runnable() {
            @Override
            public void run() {
                createPopUp();
                playMusic();
            }
        });


    }

    private void createPopUp() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup, null);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        pw = new PopupWindow(layout,(int)(width*.8), (int)(height*.55), false);
        pw.showAtLocation(findViewById(R.id.play_activity), Gravity.CENTER, 0,0);
        pw.update();
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        if(eventType.equals(EventType.PLAY)){
            Log.d("PlayBack", "TIme is: " + System.currentTimeMillis());
        }
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
                //mPlayer.play(playlist.getList().get(0).getUri());
                for(int j = 0; j < numberOfSongs;){
                    for (int i = 0; i < playlist.getList().size() && j < numberOfSongs; i++){
                        Track currentTrack = playlist.getList().get(i);
                        mPlayer.queue(currentTrack.getUri());
                        listOfSongNamesAndArtist.add(currentTrack.getName() + " by " + currentTrack.getArtist());
                        mPlayer.queue("spotify:track:3FS2e59gXFXrcg7sN2mL5z");
                        j++;
                    }
                }

                Log.d("Player", "Added all to queue");
                mPlayer.pause();

                songNumber = 0;
                paused = true;
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
        if(!paused && !betweenSongs){
            paused = true;
            long elapsedTime = System.currentTimeMillis() - startTime;
            timeLeftOfSong -= elapsedTime;
            mPlayer.pause();

            countdown.cancel();

            Log.d("PAUSE", "Elapsed time before pause: " + elapsedTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
        }
    }

    public void playClick (View view){
        if(paused){
            paused = false;
            mPlayer.resume();
            Log.d("Play", "HELLO" + System.currentTimeMillis());
            startTime = System.currentTimeMillis();
            Log.d("PLAY", "Start time: " + startTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
            startCountDown();
        }
    }

    private void startCountDown() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (countdown != null){
                    countdown.cancel();
                }
                countdown = new CountDownTimer(timeLeftOfSong, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownText.setText("" + millisUntilFinished/1000);
                    }

                    @Override
                    public void onFinish() {
                        countDownText.setText("Shot!");
                        betweenSongs = true;
                        vib.vibrate(3000);
                        mPlayer.skipToNext();
                        //TODO: Start song after x seconds
                       // mPlayer.pause();
                       // mPlayer.seekToPosition(7000);
                        //mPlayer.resume();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                nextSong();
                            }
                        },7000);
                    }
                }.start();
            }
        });
    }

    public void nextSong(){
        if (songNumber < numberOfSongs-1){
            mPlayer.skipToNext();
            timeLeftOfSong = standartTime;
            startTime = System.currentTimeMillis();
            songNumber ++;
            //TODO: Fix so it doesn't crash here
            writeSongsToScreen();
            //PlayActivity.this.runOnUiThread();
            Log.d("NEXT SONG", "Start time: " + startTime/1000 + "s\n" + "Time Left of Song: " + timeLeftOfSong/1000 + "s");
            startCountDown();
            betweenSongs = false;
        }
    }


    private void writeSongsToScreen(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(songNumber < numberOfSongs){
                    textView.setText(listOfSongNamesAndArtist.get(songNumber));
                    songsLeftText.setText("Shots left: " + (numberOfSongs - songNumber));
                }
            }
        });
    }
    public void listSizeClick(View v) {
        numberOfSongs = playlist.getList().size();
        pw.dismiss();
        writeSongsToScreen();
    }
    public void sixtyClick(View v) {
        numberOfSongs = 60;
        pw.dismiss();
        writeSongsToScreen();
    }
    public void hundredClick(View v) {
        numberOfSongs = 100;
        pw.dismiss();
        writeSongsToScreen();
    }

}
package cristianosoriobretti.powerhourwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class PlayActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback{

    private static final String CLIENT_ID = "dc83b9c7f6ab47c299c90a43edc62d18";
    private Player mPlayer;
    private User user;
    private Playlist playlist;
    private ArrayList<String> listOfSongNamesAndArtist;

    TextView currentSongText;
    TextView songsLeftText;
    TextView countDownText;
    Button playPauseBtn;

    CountDownTimer countdown;
    Timer timer;
    long timeLeftOfSong;
    long startTime;
    boolean paused;
    boolean betweenSongs;

    Vibrator vib;
    int songNumber;
    TextView textTest;

    int numberOfSongs = 60;
    final int standartTime = 10000;
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

        currentSongText = (TextView) findViewById(R.id.currentSongText);
        songsLeftText = (TextView) findViewById(R.id.songLeftText);
        countDownText = (TextView) findViewById(R.id.countDownText);
        playPauseBtn = (Button) findViewById(R.id.playPauseButton);


        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        int i = intent.getIntExtra("id", 0);
        playlist = user.getListOfPlaylists().get(i);
        playMusic();

        promptUserForGameLength();

    }

    private void promptUserForGameLength() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose length of the game");
        CharSequence[] choices = {"60", "100", "Fuck me up fam"};
        builder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        numberOfSongs = 60;
                        break;
                    case 1:
                        numberOfSongs = 100;
                        break;
                    case 2:
                        numberOfSongs = playlist.getList().size();
                        break;
                    default:
                        numberOfSongs = 60;
                        break;
                }
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeSongsToScreen();
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    public void pauseClick (){
        if(!paused && !betweenSongs){
            paused = true;
            long elapsedTime = System.currentTimeMillis() - startTime;
            timeLeftOfSong -= elapsedTime;
            mPlayer.pause();

            countdown.cancel();
            playPauseBtn.setText("Play");
            playPauseBtn.setBackgroundColor(Color.rgb(2,180,197));

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
            playPauseBtn.setText("Pause");
            playPauseBtn.setBackgroundColor(Color.rgb(255,133,0));
        } else {
            pauseClick();
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
                            }, 7000);

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
            writeSongsToScreen();
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
                    currentSongText.setText(listOfSongNamesAndArtist.get(songNumber));
                    songsLeftText.setText("Shots left: " + (numberOfSongs - songNumber));
                }
            }
        });
    }

    public void backToPlayLists(View v){
        if (countdown != null){
           countdown.cancel();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("No progress will be saved, and you will have to start over.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlayActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        backToPlayLists(new View(this));
    }
}
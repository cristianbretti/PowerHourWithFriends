package cristianosoriobretti.powerhourwithfriends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {



    private TextView textViewUser;
    private String token;
    private User user;
    private LinearLayout layoutRow;
    private RelativeLayout relView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_login);
        textViewUser = (TextView) findViewById(R.id.textViewUser);
        layoutRow = (LinearLayout) findViewById(R.id.layoutRow);
        relView = (RelativeLayout) findViewById(R.id.mainView);

        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        populateScrollView();
    }


    private void populateScrollView(){
        ArrayList<Playlist> list = user.getListOfPlaylists();
        for(int i = 0; i < list.size(); i++){
            LinearLayout layoutPlaylist =  new LinearLayout(this);
            layoutPlaylist.setOrientation(LinearLayout.HORIZONTAL);

            String nameOfPlaylist = list.get(i).getName();
            final TextView view = new TextView(this);
            view.setId(i);
            view.setText(nameOfPlaylist);
            //Set the correct layout
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0,0,0,0);
            view.setLayoutParams(llp);
            view.setTextSize(20);
            view.setTextColor(Color.WHITE);
            view.setPadding(22,0,22,0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = view.getId();
                    if (user.getListOfPlaylists().get(id).getList().size() > 0 ) {
                        Intent intent = new Intent(PlaylistActivity.this, PlayActivity.class);


                        intent.putExtra("user", user);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    } else {
                        emptyListDialog();
                    }
                }
            });
            final TextView arrow = new TextView(this);
            arrow.setId(i);
            arrow.setText(">");
            LinearLayout.LayoutParams arrllp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            arrllp.setMargins(0,0,0,0);
            arrow.setLayoutParams(arrllp);
            arrow.setTextSize(20);
            arrow.setTextColor(Color.WHITE);
            arrow.setGravity(Gravity.RIGHT);
            arrow.setPadding(22,0,22,0);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = arrow.getId();
                    if (user.getListOfPlaylists().get(id).getList().size() > 0 ) {
                        Intent intent = new Intent(PlaylistActivity.this, PlayActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else {
                        emptyListDialog();
                    }
                }
            });
            if (i == 0) {
                view.setPadding(22,50,22,0);
                arrow.setPadding(22,50,22,0);
            }
            layoutPlaylist.addView(view);
            layoutPlaylist.addView(arrow);




            layoutRow.addView(layoutPlaylist);
            ImageView line = new ImageView(this);
            line.setImageResource(R.mipmap.line);
            LinearLayout.LayoutParams imagellp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    100);

            line.setLayoutParams(imagellp);
            line.setScaleType(ImageView.ScaleType.CENTER_CROP);
            layoutRow.addView(line);
        }
    }

    private void emptyListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Empty playlist");
        builder.setMessage("This playlist does not contain any songs!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void logout(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent logoutIntent = new Intent(PlaylistActivity.this, LoginActivity.class);
                logoutIntent.putExtra("f", 1);
                startActivity(logoutIntent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
package cristianosoriobretti.powerhourwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        TextView textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        User user = (User) intent.getParcelableExtra("user");
        int i = intent.getIntExtra("id", 0);
        String text = ""  + i + user.getUserName();
        textView.setText(text);

    }
}

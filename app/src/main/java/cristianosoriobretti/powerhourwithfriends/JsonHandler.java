package cristianosoriobretti.powerhourwithfriends;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Cristian on 2016-06-21.
 */
public class JsonHandler {
    private String resultString;
    private final String userURL = "https://api.spotify.com/v1/me";

    public String createUser(String oAuthCode){

        try {
           return new JSONTask().execute(userURL, oAuthCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "Funkar EJ";
    }

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                Log.d("connectio1", connection.toString());

                connection.setRequestProperty("Authorization", "Bearer" + " " + params[1]);
                connection.connect();
                Log.d("connectio2", connection.toString());
                Integer res = connection.getResponseCode();
                Log.d("Response Code", res.toString());
                InputStream stream = connection.getInputStream();

                Log.d("Strem", stream.toString());
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return  buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("RESULT FROM ONPOSTE: ", result);
        }
    }
}

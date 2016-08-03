package cristianosoriobretti.powerhourwithfriends;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Cristian on 2016-06-21.
 */
public class JsonHandler {
    private String resultString;
    private final String userURL = "https://api.spotify.com/v1/me";
    private final String playlistsURL = "https://api.spotify.com/v1/users/";

    public User createUser(String oAuthCode){
        Log.d("Ceateduser token is :", oAuthCode);
        String userJSON = getUserJSONString(oAuthCode);

        try {
            //Get the user info
            JSONObject jsonUser = new JSONObject(userJSON);
            String userName = jsonUser.getString("display_name");
            String userID = jsonUser.getString("id");

            //Get the playlist info of the user
            String playlistsJSON = getPlaylistsJSONString(oAuthCode, userID); //json-text
            JSONObject jsonPlaylists = new JSONObject(playlistsJSON);
            JSONArray listOfPlaylistObjects = jsonPlaylists.getJSONArray("items");
            ArrayList<Playlist> listOfPlaylists = new ArrayList<>();
            for (int i = 0; i < listOfPlaylistObjects.length(); i++){
                JSONObject current = listOfPlaylistObjects.getJSONObject(i);
                String playlistName = current.getString("name");
                String playlistID = current.getString("id");
                Log.d("PlaylistID: ", playlistID);
                //Get the info from the current playlist
                String playlistJSON = getPlaylistJSONString(oAuthCode, userID, playlistID);
                if(playlistJSON == null){
                    continue;
                }
                JSONObject jsonPlaylist = new JSONObject(playlistJSON);
                JSONArray listOfTrackObjects = jsonPlaylist.getJSONArray("items");
                ArrayList<Track> listOfTracks = new ArrayList<>();
                for (int j = 0; j < listOfTrackObjects.length(); j++){
                    JSONObject currentItem = listOfTrackObjects.getJSONObject(j);
                    JSONObject currentTrack = currentItem.getJSONObject("track");
                    String trackName = currentTrack.getString("name");
                    String trackURI = currentTrack.getString("uri");
                    JSONArray artists = currentTrack.getJSONArray("artists");
                    JSONObject art = artists.getJSONObject(0);
                    String trackArtist = art.getString("name");

                    Track track = new Track(trackName, trackArtist, trackURI);
                    listOfTracks.add(track);

                }
                //TODO spara playlists object
                Playlist playlist = new Playlist(playlistID, playlistName, listOfTracks);
                listOfPlaylists.add(playlist);
            }
            //TODO skapa user object med playlist i sig
            User currentUser = new User(userName, oAuthCode, listOfPlaylists);
            return currentUser;

        } catch (JSONException e){
            Log.d("JSONException", e.getMessage());
        }
        return null;
    }

    private String getPlaylistJSONString(String oAuthCode, String userID, String playlistID){
        try {
            String url = playlistsURL + userID + "/playlists/" + playlistID + "/tracks";
            Log.d("oAuthCode", oAuthCode);
            return new JSONTask().execute(url, oAuthCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("Exception", "DÄR");
        } catch (ExecutionException e) {
            Log.d("Exception", "HÄR");
            e.printStackTrace();
        }
        return "Funkar EJ";
    }

    private String getUserJSONString(String oAuthCode){
        try {
           return new JSONTask().execute(userURL, oAuthCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "Funkar EJ";
    }

    private String getPlaylistsJSONString(String oAuthCode, String userID){
        try {
            String url = playlistsURL + userID +"/playlists";
            return new JSONTask().execute(url, oAuthCode).get();
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
                if(res.toString().equals("404")){
                    return null;
                }
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
            Log.d("RESULT FROM ONPOSTE: ", "" + result);
        }
    }
}

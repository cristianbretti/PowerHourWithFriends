package cristianosoriobretti.powerhourwithfriends;

import java.util.ArrayList;

/**
 * Created by Cristian on 2016-06-21.
 */
public class User {


    String userName;
    String authCode;
    ArrayList <Playlist> listOfPlaylists;

    public User(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public ArrayList<Playlist> getListOfPlaylists() {
        return listOfPlaylists;
    }

    public void setListOfPlaylists(ArrayList<Playlist> listOfPlaylists) {
        this.listOfPlaylists = listOfPlaylists;
    }
}

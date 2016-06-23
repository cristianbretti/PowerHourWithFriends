package cristianosoriobretti.powerhourwithfriends;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Cristian on 2016-06-21.
 */
public class User implements Parcelable {

    String userName;
    String authCode;
    ArrayList <Playlist> listOfPlaylists;

    public User(String userName, String authCode, ArrayList<Playlist> listOfPlaylists){
        this.userName = userName;
        this.authCode = authCode;
        this.listOfPlaylists = listOfPlaylists;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(listOfPlaylists);
        out.writeString(userName);
        out.writeString(authCode);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in){
            return new User(in);
        }

        public User[] newArray(int size){
            return new User[size];
        }
    };


    private User(Parcel in){
        listOfPlaylists = (ArrayList<Playlist>) in.readSerializable();
        userName = in.readString();
        authCode = in.readString();
    }
}
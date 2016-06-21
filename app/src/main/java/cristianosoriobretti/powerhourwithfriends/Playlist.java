package cristianosoriobretti.powerhourwithfriends;

import java.util.ArrayList;

/**
 * Created by Cristian on 2016-06-21.
 */
public class Playlist {
    String id;
    ArrayList<Track> list;

    public Playlist (){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Track> getList() {
        return list;
    }

    public void setList(ArrayList<Track> list) {
        this.list = list;
    }
}

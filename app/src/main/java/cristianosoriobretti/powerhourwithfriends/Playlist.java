package cristianosoriobretti.powerhourwithfriends;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cristian on 2016-06-21.
 */
public class Playlist implements Serializable {
    String id;
    String name;
    ArrayList<Track> list;
   // ArrayList<Track> list;

    public Playlist (String id, String name, ArrayList<Track> list){
        this.id = id;
        this.name = name;
        this.list = list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Track> getList() {
        return list;
    }

    public void setList(ArrayList<Track> list) {
        this.list = list;
    }
}

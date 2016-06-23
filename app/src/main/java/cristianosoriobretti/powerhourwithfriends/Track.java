package cristianosoriobretti.powerhourwithfriends;

import java.io.Serializable;

/**
 * Created by Cristian on 2016-06-21.
 */
public class Track implements Serializable{
    String name;
    String artist;
    String uri;

    public Track(String name, String artist, String uri){
        this.name = name;
        this.artist = artist;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}

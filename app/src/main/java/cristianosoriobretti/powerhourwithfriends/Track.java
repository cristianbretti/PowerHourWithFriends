package cristianosoriobretti.powerhourwithfriends;

/**
 * Created by Cristian on 2016-06-21.
 */
public class Track {
    String name;
    String uri;

    public Track(String name, String uri){
        this.name = name;
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
}

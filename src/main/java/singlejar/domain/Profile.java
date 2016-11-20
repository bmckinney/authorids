package singlejar.domain;

/**
 * Created by bmckinney on 11/19/16.
 */
public class Profile {
    
    String type;
    String id;
    String affiliation;
    int works;
    String link;
    String label;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public int getWorks() {
        return works;
    }

    public void setWorks(int works) {
        this.works = works;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "type='" + type + "', " +
                "id='" + type + "', " +
                "affiliation='" + affiliation + "', " +
                "works=" + works + ", " +
                "link='" + link + "', " +
                "label='" + label + "'" +
                '}';
    }
}

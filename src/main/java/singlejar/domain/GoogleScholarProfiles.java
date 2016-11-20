package singlejar.domain;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmckinney on 11/20/16.
 */
public class GoogleScholarProfiles {

    List<Profile> profiles = new ArrayList<>();

    public GoogleScholarProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "http://scholar.google.com.my/citations?" +
                    "hl=en&view_op=search_authors&mauthors="+URLEncoder.encode(gn+" "+sn,"UTF-8");
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href="/citations?user=ogK4ZGQAAAAJ&amp;hl=en"><span class="gs_hlt">Peter Suber</span></a>
            Pattern linkTag = Pattern.compile("<a href=\"/citations([^\"]+)\"><span class=\'gs_hlt\'>(.*?)</span></a>");
            Pattern authorId = Pattern.compile("user=([A-Za-z0-9]+)");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                Matcher idMatch = authorId.matcher(linkMatch.group(1));
                if (idMatch.find()) {
                    String personUrl = "http://scholar.google.com.my/citations?user=" + idMatch.group(1) +"&hl=en";
                    Profile p = new Profile();
                    p.setAffiliation(scrapeAffiliations(personUrl));
                    p.setType("google");
                    p.setId(idMatch.group(1));
                    p.setLink(personUrl);
                    p.setLabel(ProfileUtils.removeTags(linkMatch.group(2)));
                    p.setWorks(0);
                    profiles.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String scrapeAffiliations(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern affTag = Pattern.compile("<a href=\"/citations\\?view_op=view_org[^>]+>([^<]+)");
        Matcher affMatch = affTag.matcher(html);
        if (affMatch.find())
            return affMatch.group(1).trim();
        return "unknown";
    }

}

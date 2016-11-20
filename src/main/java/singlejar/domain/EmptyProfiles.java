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
public class EmptyProfiles {

    List<Profile> profiles = new ArrayList<>();

    public EmptyProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "http://www.website.com/search?" +
                    "firstname=" + URLEncoder.encode(gn,"UTF-8") +
                    "&lastname=" + URLEncoder.encode(sn,"UTF-8");
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href='profiles/people/0006018269C6431A60A5?q=firstname%3Ajane+lastname%3Asmith'>
            // <span class="authorName">Jane M. Smith</span>
            Pattern linkTag = Pattern.compile(
                    "<a href='profiles/people/([A-Za-z0-9]+)[^']+'>\\s*<span class=\"authorName\">([^<]+)</span>");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                String personUrl = "http://www.website.com/people/" + linkMatch.group(1);
                Profile p = new Profile();
                p.setAffiliation(scrapeAffiliations(personUrl));
                p.setType("empty");
                p.setId(linkMatch.group(1));
                p.setLink(personUrl);
                p.setLabel(linkMatch.group(2));
                p.setWorks(scrapeWorks(personUrl));
                profiles.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int scrapeWorks(String url) {

        String html = ProfileUtils.fetchHtml(url);
        int worksCount = 0;

        // scrape for works
        Pattern worksTag = Pattern.compile("<span class=\"publicationTitle\">");
        Matcher worksMatch = worksTag.matcher(html);
        while (worksMatch.find())
            worksCount++;
        
        return worksCount;
    }

    private String scrapeAffiliations(String url) {

        String html = ProfileUtils.fetchHtml(url);
        String aff = "unknown";

        // scrape for affiliation
        Pattern affTag = Pattern.compile("<b>Affiliation:</b></td><td[^>]+>([^<]+)<");
        Matcher affMatch = affTag.matcher(html.toString());
        if (affMatch.find())
            aff = affMatch.group(1);

        return aff.replaceAll("&nbsp;", " ").trim();
    }

}

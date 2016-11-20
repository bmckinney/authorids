package singlejar.domain;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProquestProfiles {

    List <Profile> profiles = new ArrayList<>();

    public ProquestProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
    
    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "http://www.scholaruniverse.com/namesearch?" +
                    "f=" + URLEncoder.encode(gn,"UTF-8") +
                    "&m=&l=" + URLEncoder.encode(sn,"UTF-8");

            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href='profiles/people/668365EFC0A80006018269C6431A60A5?q=firstname%3Astuart+lastname%3Ashieber'>
            // <span class="scholarName">Stuart M. Shieber</span>
            Pattern linkTag = Pattern.compile(
                    "<a href='profiles/people/([A-Za-z0-9]+)[^']+'>\\s*<span class=\"scholarName\">([^<]+)</span>");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                String personUrl = "http://www.scholaruniverse.com/profiles/people/" + linkMatch.group(1);
                Profile p = new Profile();
                p.setAffiliation(scrapeAffiliations(personUrl));
                p.setType("proquest");
                p.setId(linkMatch.group(1));
                p.setLink(personUrl);
                p.setLabel(linkMatch.group(2).replaceAll("_", " "));
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
        Pattern worksTag = Pattern.compile("<span class=\"pubtitle\">");
        Matcher worksMatch = worksTag.matcher(html);
        while (worksMatch.find())
            worksCount++;

        Pattern addPubs = Pattern.compile("<li>(\\d+) Additional Publications</li>");
        Matcher addPubsMatch = addPubs.matcher(html);
        if (addPubsMatch.find())
            worksCount = worksCount + Integer.parseInt(addPubsMatch.group(1));

        return worksCount;
    }

    private String scrapeAffiliations(String url) {

        String html = ProfileUtils.fetchHtml(url);
        String aff = "unknown";
        
        // scrape for affiliation
        Pattern affTag = Pattern.compile("<b>Affiliation:</b>&nbsp;&nbsp;</td><td[^>]+>([^<]+)<");
        Matcher affMatch = affTag.matcher(html.toString());
        if (affMatch.find())
            aff = affMatch.group(1);

        return aff.replaceAll("&nbsp;", " ").trim();
    }

}

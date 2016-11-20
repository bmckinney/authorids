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
public class ViafProfiles {

    List <Profile> profiles = new ArrayList<>();

    public ViafProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
    
    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "http://viaf.org/viaf/search?query=local.personalNames+all+%22" +
                    URLEncoder.encode(gn+" "+sn,"UTF-8") +
                    "%22+and+local.sources+any+%22lc%22&stylesheet=/viaf/xsl/results.xsl&sortKeys=holdingscount" +
                    "&maximumRecords=100&httpAccept=text/html";
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href="/viaf/191582424/#Shieber,_Stuart_M.">Shieber, Stuart M.
            Pattern linkTag = Pattern.compile("<a href=\"/viaf/([0-9]+)/#([^\"]+)\">");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                String composedName = linkMatch.group(2).replaceAll("_"," ");
                if (composedName.toLowerCase().contains(sn.toLowerCase()) &&
                        composedName.toLowerCase().contains(gn.toLowerCase())) {
                    String personUrl = "http://viaf.org/viaf/" + linkMatch.group(1);
                    Profile p = new Profile();
                    p.setAffiliation("unknown");
                    p.setType("viaf");
                    p.setId(linkMatch.group(1));
                    p.setLink(personUrl);
                    p.setLabel(linkMatch.group(2).replaceAll("_"," "));
                    p.setWorks(scrapeWorks(personUrl));
                    profiles.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int scrapeWorks(String url) {

        String html = ProfileUtils.fetchHtml(url);

        Pattern worksTag = Pattern.compile("<div class=\"dataTables_info\" id=\"workTable_info\" role=\"status\" aria-live=\"polite\">Showing 1 to \\d+ of (\\d+) entries</div>");
        Matcher worksMatch = worksTag.matcher(html);
        if (worksMatch.find())
            return Integer.parseInt(worksMatch.group(1));
        
        return 0;
    }

}

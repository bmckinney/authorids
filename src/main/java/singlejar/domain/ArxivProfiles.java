package singlejar.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmckinney on 11/20/16.
 */
public class ArxivProfiles {

    List<Profile> profiles = new ArrayList<>();

    public ArxivProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String author = sn+"_"+gn;
            author = author.replaceAll("-","_").replaceAll(" ","_");
            String url = "http://arxiv.org/find/all/1/au:+"+author+"/0/1/0/all/0/1";
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href="/find/cs/1/au:+Shieber_S/0/1/0/all/0/1">Stuart M. Shieber</a>
           Pattern linkTag = Pattern.compile("<a href=\"/find/[^/]+/1/au:\\+([A-Za-z0-9_]+)/0/1/0/all/0/1\">(.*?)</a>");
            Matcher linkMatch = linkTag.matcher(html);
            String foundIds = "";
            while (linkMatch.find()) {
                if (!foundIds.contains(linkMatch.group(1))) {
                    String linkText =  ProfileUtils.removeTags(linkMatch.group(2)).toLowerCase();
                    if (linkText.contains(sn.toLowerCase()) && linkText.contains(gn.toLowerCase())) {
                        foundIds += linkMatch.group(1);
                        String personUrl = "http://arxiv.org/find/cs/1/au:+"+linkMatch.group(1)+"/0/1/0/all/0/1";
                        Profile p = new Profile();
                        p.setAffiliation("unknown");
                        p.setType("arxiv");
                        p.setId(linkMatch.group(1));
                        p.setLink(personUrl);
                        p.setLabel(ProfileUtils.removeTags(linkMatch.group(2)));
                        p.setWorks(0);
                        profiles.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

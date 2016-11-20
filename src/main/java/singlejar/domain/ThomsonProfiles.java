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
public class ThomsonProfiles {

    List<Profile> profiles = new ArrayList<>();

    public ThomsonProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "http://www.researcherid.com/ProfileSearch.action?" +
                    "searchTabLoaded=true&criteria.lastName="+URLEncoder.encode(sn,"UTF-8")+
                    "&criteria.firstName="+URLEncoder.encode(gn,"UTF-8")+ "&__checkbox_criteria.includeOtherName=true"+
                    "&criteria.institution=&__checkbox_criteria.includePastInstitutions=true&criteria.country="+
                    "Select%20a%20Country%20%2F%20Territory%3A&criteria.keywords=&criteria.researcherId=&_=";
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            // <a href="javascript:void(0);" class="smallV110"
            //title="F-8986-2012" onclick="navigateTo('F-8986-2012')">Blatt Sebastian</a>
            //onclick="navigateTo('F-8986-2012')">Blatt Sebastian</a>
            Pattern linkTag = Pattern.compile("onclick=\"navigateTo\\('([^']+)'\\)\">(.*?)</a>");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                String personUrl = "http://www.researcherid.com/rid/" + linkMatch.group(1);
                Profile p = new Profile();
                p.setAffiliation(scrapeAffiliations(personUrl));
                p.setType("thomson");
                p.setId(linkMatch.group(1));
                p.setLink(personUrl);
                p.setLabel(ProfileUtils.removeTags(linkMatch.group(2)));
                p.setWorks(scrapeWorks(personUrl));
                profiles.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int scrapeWorks(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern worksTag = Pattern.compile("<label id=\"current_metadata_total\">(\\d+)</label>");
        Matcher worksMatch = worksTag.matcher(html);
        if (worksMatch.find())
            return Integer.parseInt(worksMatch.group(1));
        return 0;
    }

    private String scrapeAffiliations(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern worksTag = Pattern.compile("<label id=\"ProfileView_researcher_institution\">([^<]+)</label>");
        Matcher worksMatch = worksTag.matcher(html);
        if (worksMatch.find())
            return worksMatch.group(1).trim();
        return "unknown";
    }

}

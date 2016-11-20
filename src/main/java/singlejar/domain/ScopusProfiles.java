package singlejar.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmckinney on 11/20/16.
 */
public class ScopusProfiles {

    List<Profile> profiles = new ArrayList<>();

    public ScopusProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        try {

            // fetch html
            String url = "https://www.scopus.com/results/authorNamesList.url?" +
                    "sot=al&sdt=al&sl=87&selectionPageSearch=anl&reselectAuthor=false&activeFlag=false" +
                    "&showDocument=false&resultsPerPage=20&offset=1&jtp=false&currentPage=1&previousSelectionCount=0" +
                    "&tooManySelections=false&previousResultCount=0&authSubject=LFSC&authSubject=HLSC" +
                    "&authSubject=PHSC&authSubject=SOSC&exactAuthorSearch=true&showFullList=false&authorPreferredName="+
                    "&origin=searchauthorfreelookup" +
                    "&s=AUTH--LAST--NAME%28EQUALS%28" + sn +"%29%29+AND+AUTH--FIRST%28"+ gn +
                    "%29&st1="+sn+"&st2="+gn;
            String html = ProfileUtils.fetchHtml(url);

            // scrape result
            Pattern linkTag = Pattern.compile("<a href=\"([^\"]+)\" title=\"View author [^\"]+\"\\s*>(.*?)</a>");
            Pattern scopusId = Pattern.compile("authorID=(\\d+)");
            Matcher linkMatch = linkTag.matcher(html);
            while (linkMatch.find()) {
                Matcher idMatch = scopusId.matcher(linkMatch.group(1));
                if (idMatch.find()) {
                    String personUrl = "https://www.scopus.com/authid/detail.url?authorId="+idMatch.group(1);
                    Profile p = new Profile();
                    p.setAffiliation(scrapeAffiliations(personUrl));
                    p.setType("scopus");
                    p.setId(idMatch.group(1));
                    p.setLink(personUrl);
                    p.setLabel(linkMatch.group(2));
                    p.setWorks(scrapeWorks(personUrl));
                    profiles.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int scrapeWorks(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern worksTag = Pattern.compile("<a href=\"#\" id=\"docCntLnk\"[^>]+>(\\d+)</a>");
        Matcher worksMatch = worksTag.matcher(html);
        if (worksMatch.find())
            return Integer.parseInt(worksMatch.group(1));
        return 0;
    }

    private String scrapeAffiliations(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern affTag = Pattern.compile("<div class=\"authAffilcityCounty\">([^<]+)<");
        Matcher affMatch = affTag.matcher(html.toString());
        if (affMatch.find())
            return affMatch.group(1).trim();
        return "unknown";
    }
}

package singlejar.domain;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmckinney on 11/20/16.
 */
public class OrcidProfiles {

    List<Profile> profiles = new ArrayList<>();

    public OrcidProfiles(String surname, String givenname) {
        scrapeProfiles(surname, givenname);
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    private void scrapeProfiles(String sn, String gn) {

        String SEARCH_RESULTS_PATH = "$.orcid-search-results.orcid-search-result[*]";
        String BIO_PATH = "$.orcid-profile.orcid-bio";
        String PERSONAL_DETAILS_PATH = BIO_PATH + ".personal-details";
        String ORCID_PATH = "$.orcid-profile.orcid-identifier.path";
        String FAMILY_NAME_PATH = PERSONAL_DETAILS_PATH + ".family-name.value";
        String GIVEN_NAMES_PATH = PERSONAL_DETAILS_PATH + ".given-names.value";

        try {
            
            // fetch json
            String url = "http://pub.orcid.org/v1.1/search/orcid-bio?q=" +
                    "family-name:" + URLEncoder.encode(sn,"UTF-8") +
                    "+AND+given-names:" + URLEncoder.encode(gn,"UTF-8");
            String json = ProfileUtils.fetchHtml(url);
            
            List<JSONObject> results = JsonPath.read(json,SEARCH_RESULTS_PATH);
            Iterator<JSONObject> it = results.iterator();

            while (it.hasNext()) {
                String s = it.next().toJSONString();
                String personUrl = "http://orcid.org/"+ JsonPath.read(s, ORCID_PATH);
                Profile p = new Profile();
                p.setAffiliation("unknown");
                p.setType("orcid");
                p.setId(JsonPath.read(s, ORCID_PATH).toString());
                p.setLink(personUrl);
                p.setLabel(JsonPath.read(s, FAMILY_NAME_PATH) + ", " + JsonPath.read(s, GIVEN_NAMES_PATH));
                p.setWorks(scrapeWorks(personUrl));
                profiles.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int scrapeWorks(String url) {

        String html = ProfileUtils.fetchHtml(url);
        Pattern worksTag = Pattern.compile("orcidVar\\.workIds = JSON\\.parse\\(\"\\[([^\\]]+)\\]");
        Matcher worksMatch = worksTag.matcher(html);
        if (worksMatch.find())
            return worksMatch.group(1).split(",").length;
        return 0;
    }

}

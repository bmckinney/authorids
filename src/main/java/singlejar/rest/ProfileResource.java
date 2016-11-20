package singlejar.rest;

import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import singlejar.domain.ArxivProfiles;
import singlejar.domain.GoogleScholarProfiles;
import singlejar.domain.OrcidProfiles;
import singlejar.domain.Profile;
import singlejar.domain.ProquestProfiles;
import singlejar.domain.ScopusProfiles;
import singlejar.domain.ThomsonProfiles;
import singlejar.domain.ViafProfiles;

import java.util.ArrayList;
import java.util.List;

@Component
@RestxResource
public class ProfileResource {
    
    @GET("/profiles")
    @PermitAll
    public List<Profile> findProfiles(String surname, String givenname) {
        List <Profile> profiles = new ArrayList<>();
        
        // PROQUEST
        ProquestProfiles proquestProfiles = new ProquestProfiles(surname, givenname);
        profiles.addAll(proquestProfiles.getProfiles());
        
        // VIAF
        ViafProfiles viafProfiles = new ViafProfiles(surname, givenname);
        profiles.addAll(viafProfiles.getProfiles());
        
        // THOMSON
        ThomsonProfiles thomsonProfiles = new ThomsonProfiles(surname, givenname);
        profiles.addAll(thomsonProfiles.getProfiles());
        
        // ARXIV
        ArxivProfiles arxivProfiles = new ArxivProfiles(surname, givenname);
        profiles.addAll(arxivProfiles.getProfiles());
        
        // SCOPUS
        ScopusProfiles scopusProfiles = new ScopusProfiles(surname, givenname); 
        profiles.addAll(scopusProfiles.getProfiles());
        
        // GOOGLE SCHOLAR
        GoogleScholarProfiles googleScholarProfiles = new GoogleScholarProfiles(surname, givenname);
        profiles.addAll(googleScholarProfiles.getProfiles());
        
        // ORCID
        OrcidProfiles orcidProfiles = new OrcidProfiles(surname, givenname);
        profiles.addAll(orcidProfiles.getProfiles());
        
        return profiles;
    }

}

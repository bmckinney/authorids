package singlejar.domain;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmckinney on 11/20/16.
 */
public class ProfileUtils {
    
    public static String fetchHtml(String url) {
        
        StringBuilder html = new StringBuilder();
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            HttpGet get = new HttpGet(url);

            // add thomson cookie?
            if (url.contains("researcherid.com")) {
                get.addHeader("Cookie", getThomsonCookie());
            }

            // add orcid accept header?
            if (url.contains("pub.orcid.org")) {
                get.addHeader("accept", "application/orcid+json");
            }
            
            HttpResponse response = client.execute(get);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                html.append(output);
            }
            client.getConnectionManager().shutdown();
        } catch (IOException ioe) {
            System.out.println("Error fetching " + url + " - " + ioe.getMessage());
            ioe.printStackTrace();
        }return html.toString();

    }
    
    public static String getThomsonCookie() {
        String url = "http://www.researcherid.com/ViewProfileSearch.action?returnCode=ROUTER.Unauthorized&SrcApp=CR&Init=Yes";
        String retval = "";
        try {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet req = new HttpGet(url);
            HttpResponse res = client.execute(req);

            if (res.getStatusLine().getStatusCode() != 200) {
                return "";
            }

            Header[] headers = res.getHeaders("Set-Cookie");
            for (int i = 0; i < headers.length; i++) {
                if (!(retval == "")) retval += " ";
                retval += headers[i].getValue().replaceAll("; Version=1","").replaceAll("; Path=/; HttpOnly","");
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retval;
    }

    public static String removeTags(String string) {

        String retval = string;
        Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
        if (string == null || retval.length() == 0) {
            return retval;
        }

        Matcher m = REMOVE_TAGS.matcher(retval);
        retval = m.replaceAll("");
        retval = retval.replaceAll("&nbsp;", " ").trim();
        return retval;
    }
    
}

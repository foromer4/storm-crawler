package com.digitalpebble.stormcrawler.util;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import com.ibm.icu.text.SimpleDateFormat;


/**
 * Helper to extract cookies from cookies string.
 * based on Nutch.  @see <a href= https://gist.githubusercontent.com/jnioche/6141308519694b5c57d4fbd45d5990ac/raw/e7a9544131e9b2e7a542d9caea98dfd663c9e336/CookieConverter.java>CookieConverter</a>
 * 
 * @author OSchliefer
 *
 */
public class CookieConverter {		
	
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
		      "EEE, dd MMM yyyy HH:mm:ss zzz");	
	

	 /**
	  * Get a list of cookies based on the cookies string taken from response header and the target url.
	  * @param cookiesString the value of the http header for "Cookie" in the http response.
	  * @param targetURL the url for which we wish to pass the cookies in the request.
	  * @return List off cookies to add to the request.
	  */
	  public static List<Cookie> getCookies(String cookiesString, URL targetURL) {
	    ArrayList<Cookie> list = new ArrayList<Cookie>();

	    String[] cookiestrings = cookiesString.toString().split("\t");
	    
	   
	    for (String cs : cookiestrings) {
	      String name = null;
	      String value = null;

	      String expires = null;
	      String domain = null;
	      String path = null;

	      boolean secure = false;

	      String[] tokens = cs.split(";");

	      int equals = tokens[0].indexOf("=");
	      name = tokens[0].substring(0, equals);
	      value = tokens[0].substring(equals + 1);

	      for (int i = 1; i < tokens.length; i++) {
	        String ti = tokens[i].trim();
	        if (ti.equalsIgnoreCase("secure"))
	          secure = true;
	        if (ti.toLowerCase().startsWith("path=")) {
	          path = ti.substring(5);
	        }
	        if (ti.toLowerCase().startsWith("domain=")) {
	          domain = ti.substring(7);
	        }
	        if (ti.toLowerCase().startsWith("expires=")) {
	          expires = ti.substring(8);
	        }
	      }

	      BasicClientCookie cookie = new BasicClientCookie(name, value);

	      // check domain
	      if (domain != null) {
	        cookie.setDomain(domain);

	        if (!targetURL.getHost().contains(domain))
	          continue;
	      }

	      // check path
	      if (path != null) {
	        cookie.setPath(path);

	        if (!targetURL.getPath().startsWith(path))
	          continue;
	      }

	      // check secure
	      if (secure) {
	        cookie.setSecure(secure);

	        if (!targetURL.getProtocol().equalsIgnoreCase("https"))
	          continue;
	      }

	      // check expiration
	      if (expires != null) {
	        try {
	          Date expirationDate = DATE_FORMAT.parse(expires);

	          // check that it hasn't expired?
	          if (cookie.isExpired(new Date()))
	            continue;

	          cookie.setExpiryDate(expirationDate);
	        } catch (ParseException e) {
	          // ignore exceptions
	        }
	      }

	      // attach additional infos to cookie
	      list.add(cookie);
	    }

	    return list;
	  }

}

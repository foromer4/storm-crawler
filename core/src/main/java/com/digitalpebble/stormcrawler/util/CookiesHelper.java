package com.digitalpebble.stormcrawler.util;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.ibm.icu.text.SimpleDateFormat;

public class CookiesHelper {		
	
	public static final String COOKIES_HEADER = "cookie";
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
		      "EEE, dd MMM yyyy HH:mm:ss zzz");
	
	 public static String extractCookiesString(HttpResponse response) {
		  HeaderIterator iter = response.headerIterator();
	        while (iter.hasNext()) {
	            Header header = iter.nextHeader();
	            if (header.getName().toLowerCase(Locale.ROOT) == COOKIES_HEADER) {
	               return header.getValue();
	            }	           
	        }
	        return null;
	 }

	  public static List<Cookie> getCookies(String cookiesString, URL targetURL) {
	    ArrayList<Cookie> list = new ArrayList<Cookie>();

	    String[] cookiestrings = cookiesString.toString().split("\t");
	    
	    //taken from nutch
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

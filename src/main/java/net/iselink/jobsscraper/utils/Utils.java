package net.iselink.jobsscraper.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Utils {

	public static String cleanUrl(String url) {
		URI uri = URI.create(url);
		try {
			return new URL(uri.getScheme(), uri.getHost(), uri.getPath()).toString();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

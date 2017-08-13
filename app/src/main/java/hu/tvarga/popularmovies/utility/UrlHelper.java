package hu.tvarga.popularmovies.utility;

import hu.tvarga.popularmovies.BuildConfig;

public class UrlHelper {

	public static String getPosterUrl(String posterPath) {
		return BuildConfig.url_poster + "w185" + posterPath;
	}
}

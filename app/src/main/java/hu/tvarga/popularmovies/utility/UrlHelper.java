package hu.tvarga.popularmovies.utility;

import android.support.annotation.NonNull;

import hu.tvarga.popularmovies.BuildConfig;

public class UrlHelper {

	public static String getPosterUrl(String posterPath) {
		return BuildConfig.url_poster + "w185" + posterPath;
	}

	@NonNull
	public static String getDefaultUrl() {
		return BuildConfig.url_default + "api_key=" + BuildConfig.cfg_api_key;
	}
}

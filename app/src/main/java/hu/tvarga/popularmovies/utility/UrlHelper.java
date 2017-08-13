package hu.tvarga.popularmovies.utility;

import android.support.annotation.NonNull;

import hu.tvarga.popularmovies.BuildConfig;

public class UrlHelper {

	public static String getPosterUrl(String posterPath) {
		return BuildConfig.url_poster + "w185" + posterPath;
	}

	@NonNull
	private static String getApiKeyUrlPart() {return "api_key=" + BuildConfig.cfg_api_key;}

	@NonNull
	public static String getDefaultUrl() {
		return BuildConfig.url_default + getApiKeyUrlPart();
	}

	public static String getUrlSortByPopularity() {
		return BuildConfig.url_sort_by_popularity + "&" + getApiKeyUrlPart();
	}

	public static String getUrlSortByRating() {
		return BuildConfig.url_top_rated + getApiKeyUrlPart();
	}
}

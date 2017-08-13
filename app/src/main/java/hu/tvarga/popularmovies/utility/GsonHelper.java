package hu.tvarga.popularmovies.utility;

import com.google.gson.Gson;

public class GsonHelper {

	private static Gson gson;

	private GsonHelper() {
	}

	public static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}

		return gson;
	}
}

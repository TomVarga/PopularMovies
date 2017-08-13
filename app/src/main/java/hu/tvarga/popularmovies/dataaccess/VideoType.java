package hu.tvarga.popularmovies.dataaccess;

import com.google.gson.annotations.SerializedName;

public enum VideoType {
	@SerializedName("Trailer")
	TRAILER,
	@SerializedName("Teaser")
	TEASER,
	@SerializedName("Clip")
	CLIP,
	@SerializedName("Featurette")
	FEATURETTE,
}

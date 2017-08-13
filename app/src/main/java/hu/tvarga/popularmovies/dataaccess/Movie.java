package hu.tvarga.popularmovies.dataaccess;

import com.google.gson.annotations.SerializedName;

public class Movie extends DAO {

	private static final long serialVersionUID = -2724296208305320627L;

	@SerializedName("id")
	int id;
	@SerializedName("original_title")
	public String originalTitle;
	@SerializedName("release_date")
	public String releaseDate;
	@SerializedName("vote_average")
	public String voteAverage;
	@SerializedName("poster_path")
	public String posterPath;
	@SerializedName("overview")
	public String overview;
}

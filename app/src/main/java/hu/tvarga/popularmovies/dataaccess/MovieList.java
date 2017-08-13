package hu.tvarga.popularmovies.dataaccess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieList extends DAO {

	@SerializedName("page")
	public int page;
	@SerializedName("total_results")
	public int totalResults;
	@SerializedName("total_pages")
	public int totalPages;
	@SerializedName("results")
	public List<Movie> results;
}

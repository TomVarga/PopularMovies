package hu.tvarga.popularmovies.dataaccess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewList extends DAO {

	private static final long serialVersionUID = -3157611931382364744L;

	@SerializedName("id")
	public int id;
	@SerializedName("page")
	public Integer page;
	@SerializedName("results")
	public List<Review> results; // NOSONAR
	@SerializedName("total_pages")
	public Integer totalPages;
	@SerializedName("total_results")
	public Integer totalResults;
}

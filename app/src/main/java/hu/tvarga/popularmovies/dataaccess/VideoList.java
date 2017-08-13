package hu.tvarga.popularmovies.dataaccess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoList extends DAO {

	private static final long serialVersionUID = 6907117016132948560L;

	@SerializedName("id")
	public int id;
	@SerializedName("results")
	public List<Video> results; // NOSONAR
}

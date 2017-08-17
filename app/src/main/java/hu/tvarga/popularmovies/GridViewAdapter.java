package hu.tvarga.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hu.tvarga.popularmovies.dataaccess.Movie;

import static hu.tvarga.popularmovies.utility.UrlHelper.getPosterUrl;

class GridViewAdapter extends BaseAdapter {

	private final Context context;
	private final List<Movie> movies;

	GridViewAdapter(Context context, List<Movie> movies) {
		this.context = context;
		this.movies = movies;
	}

	@Override
	public int getCount() {
		return movies.size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View viewItem = view;

		if (viewItem == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			viewItem = inflater.inflate(R.layout.grid_item, null);
		}

		Movie movie = movies.get(i);
		ImageView imageView = viewItem.findViewById(R.id.moviePoster);
		Picasso.with(context).load(getPosterUrl(movie.posterPath)).into(imageView);

		return viewItem;
	}
}

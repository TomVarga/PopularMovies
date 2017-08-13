package hu.tvarga.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.tvarga.popularmovies.dataaccess.Movie;

import static hu.tvarga.popularmovies.GridViewFragment.MOVIE_EXTRA_KEY;

public class DetailViewFragment extends Fragment {

	Movie movie;
	private TextView title;
	private View root;

	public static DetailViewFragment newInstance(Movie movie) {
		DetailViewFragment detailFragment = new DetailViewFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(MOVIE_EXTRA_KEY, movie);
		detailFragment.setArguments(bundle);

		return detailFragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			movie = (Movie) getArguments().getSerializable(MOVIE_EXTRA_KEY);
		}
		if (savedInstanceState != null) {
			movie = (Movie) savedInstanceState.getSerializable(MOVIE_EXTRA_KEY);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_detail_view, container, false);

		title = root.findViewById(R.id.title);

		root.setVisibility(View.INVISIBLE);
		if (movie != null) {
			updateMovie(movie);
		}

		return root;
	}

	public void updateMovie(Movie movie) {
		this.movie = movie;

		root.setVisibility(View.VISIBLE);

		title.setText(movie.originalTitle);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(MOVIE_EXTRA_KEY, movie);
	}
}

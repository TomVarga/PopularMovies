package hu.tvarga.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hu.tvarga.popularmovies.dataaccess.Movie;

import static hu.tvarga.popularmovies.GridViewFragment.MOVIE_EXTRA_KEY;
import static hu.tvarga.popularmovies.utility.UrlHelper.getPosterUrl;

public class DetailViewFragment extends Fragment {

	@BindView(R.id.title)
	TextView title;
	@BindView(R.id.releaseDate)
	TextView releaseDate;
	@BindView(R.id.votes)
	TextView votes;
	@BindView(R.id.synopsis)
	TextView synopsis;
	@BindView(R.id.moviePoster)
	ImageView moviePoster;

	private Movie movie;
	private Unbinder unbinder;
	private View root;

	private void clearView() {
		TextView[] textViews = {title, releaseDate, votes, synopsis};
		for (TextView textView : textViews) {
			textView.setText("");
		}
		moviePoster.setImageDrawable(ContextCompat
				.getDrawable(this.getActivity(), android.R.drawable.ic_menu_report_image));
	}

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
		unbinder = ButterKnife.bind(this, root);

		if (movie != null) {
			updateMovie(movie);
		}

		return root;
	}

	public void updateMovie(Movie movie) {
		clearView();
		this.movie = movie;

		title.setText(movie.originalTitle);
		releaseDate.setText(movie.releaseDate);
		votes.setText(movie.voteAverage);
		synopsis.setText(movie.overview);
		Picasso.with(getActivity()).load(getPosterUrl(movie.posterPath)).into(moviePoster);

		root.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(MOVIE_EXTRA_KEY, movie);
	}
}

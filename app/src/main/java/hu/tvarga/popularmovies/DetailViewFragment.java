package hu.tvarga.popularmovies;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hu.tvarga.popularmovies.dataaccess.Movie;
import hu.tvarga.popularmovies.dataaccess.Review;
import hu.tvarga.popularmovies.dataaccess.Video;
import hu.tvarga.popularmovies.dataaccess.database.MovieContract;

import static hu.tvarga.popularmovies.GridViewFragment.MOVIE_EXTRA_KEY;
import static hu.tvarga.popularmovies.utility.UrlHelper.getPosterUrl;

public class DetailViewFragment extends Fragment {

	public static final String REVIEWS_STATE = "REVIEWS_STATE";
	public static final String VIDEOS_STATE = "VIDEOS_STATE";
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
	@BindView(R.id.reviewsContainer)
	RecyclerView recyclerViewReviews;
	@BindView(R.id.videosContainer)
	RecyclerView recyclerViewVideos;
	@BindView(R.id.favoriteCheckBox)
	CheckBox favoriteCheckBox;

	private Movie movie;
	private Unbinder unbinder;
	private View root;
	private List<Review> reviews = new ArrayList<>();
	private List<Video> videos = new ArrayList<>();
	private ReviewAdapter reviewAdapter;
	private VideoAdapter videoAdapter;
	private LinearLayoutManager recyclerViewReviewsLayoutManager;
	private LinearLayoutManager recyclerViewVideosLayoutManager;
	private Parcelable reviewState;
	private Parcelable videoState;

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
			updateExtraData();
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			reviewState = savedInstanceState.getParcelable(REVIEWS_STATE);
			videoState = savedInstanceState.getParcelable(VIDEOS_STATE);
		}
		if (movie != null) {
			updateMovie(movie);
		}
	}

	private void updateExtraData() {
		if (movie != null) {
			reviews.clear();
			reviews.addAll(movie.reviews);
			videos.clear();
			videos.addAll(movie.videos);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_detail_view, container, false);
		unbinder = ButterKnife.bind(this, root);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(root.getContext(),
				DividerItemDecoration.VERTICAL);
		reviewAdapter = new ReviewAdapter(getActivity(), reviews);
		recyclerViewReviews.setAdapter(reviewAdapter);
		recyclerViewReviews.addItemDecoration(dividerItemDecoration);
		recyclerViewReviewsLayoutManager =
				(LinearLayoutManager) recyclerViewReviews.getLayoutManager();

		videoAdapter = new VideoAdapter(getActivity(), videos);
		recyclerViewVideos.setAdapter(videoAdapter);
		recyclerViewVideos.addItemDecoration(dividerItemDecoration);
		recyclerViewVideosLayoutManager =
				(LinearLayoutManager) recyclerViewVideos.getLayoutManager();

		return root;
	}

	@NonNull
	private CompoundButton.OnCheckedChangeListener getOnCheckedListener() {
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				movie.favorite = checked;
				if (checked) {
					ContentValues movieValues = new ContentValues();
					movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
					movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
							movie.releaseDate);
					movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
							movie.voteAverage);
					movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
					movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
					movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
							movie.originalTitle);
					movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, movie.favorite);

					getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
							movieValues);
				}
				else {
					getContext().getContentResolver().delete(
							MovieContract.MovieEntry.CONTENT_URI.buildUpon()
									.appendPath(Integer.toString(movie.id)).build(), null, null);
				}
			}
		};
	}

	public void updateMovie(Movie movie) {
		clearView();
		this.movie = movie;
		updateExtraData();

		favoriteCheckBox.setOnCheckedChangeListener(null);
		favoriteCheckBox.setChecked(movie.favorite);
		favoriteCheckBox.setOnCheckedChangeListener(getOnCheckedListener());
		title.setText(movie.originalTitle);
		releaseDate.setText(movie.releaseDate);
		votes.setText(movie.voteAverage);
		synopsis.setText(movie.overview);
		Picasso.with(getActivity()).load(getPosterUrl(movie.posterPath)).into(moviePoster);

		reviewAdapter.notifyDataSetChanged();
		videoAdapter.notifyDataSetChanged();

		root.setVisibility(View.VISIBLE);

		restoreLayoutManagerPositions();
	}

	private void restoreLayoutManagerPositions() {
		if (reviewState != null) {
			recyclerViewReviewsLayoutManager.onRestoreInstanceState(reviewState);
		}
		if (videoState != null) {
			recyclerViewVideosLayoutManager.onRestoreInstanceState(videoState);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putSerializable(MOVIE_EXTRA_KEY, movie);

		Parcelable reviewsState = recyclerViewReviewsLayoutManager.onSaveInstanceState();
		outState.putParcelable(REVIEWS_STATE, reviewsState);
		Parcelable videosState = recyclerViewReviewsLayoutManager.onSaveInstanceState();
		outState.putParcelable(VIDEOS_STATE, videosState);
		super.onSaveInstanceState(outState);
	}
}

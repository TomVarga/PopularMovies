package hu.tvarga.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.tvarga.popularmovies.dataaccess.Movie;
import hu.tvarga.popularmovies.dataaccess.MovieList;
import hu.tvarga.popularmovies.dataaccess.ReviewList;
import hu.tvarga.popularmovies.dataaccess.VideoList;
import hu.tvarga.popularmovies.dataaccess.database.MovieContract;
import hu.tvarga.popularmovies.utility.GsonHelper;
import hu.tvarga.popularmovies.utility.UrlHelper;

import static hu.tvarga.popularmovies.GridViewActivity.MULTI_PANE_EXTRA_KEY;
import static hu.tvarga.popularmovies.utility.UrlHelper.getDefaultUrl;

public class GridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String[] MAIN_MOVIE_PROJECTION =
			{MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
					MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
					MovieContract.MovieEntry.COLUMN_POSTER_PATH,
					MovieContract.MovieEntry.COLUMN_OVERVIEW,
					MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,};

	public static final int INDEX_MOVIE_MOVIE_ID = 0;
	public static final int INDEX_MOVIE_RELEASE_DATE = 1;
	public static final int INDEX_MOVIE_VOTE_AVERAGE = 2;
	public static final int INDEX_MOVIE_POSTER_PATH = 3;
	public static final int INDEX_MOVIE_OVERVIEW = 4;
	public static final int INDEX_MOVIE_ORIGINAL_TITLE = 5;

	public static final int ID_MOVIE_LOADER = 44;

	private GridFragmentCallback gridFragmentCallback;
	GridViewAdapter gridViewAdapter;
	Cursor cursor;
	int position;
	public static final String MOVIE_EXTRA_KEY = "movie";

	private boolean videoDownloadCompleted;
	private boolean reviewDownloadCompleted;
	GridView gridView;

	public static GridViewFragment newInstance(boolean multiPane) {
		GridViewFragment gridViewFragment = new GridViewFragment();

		Bundle bundle = new Bundle();
		bundle.putBoolean(MULTI_PANE_EXTRA_KEY, multiPane);
		gridViewFragment.setArguments(bundle);

		return gridViewFragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);

		gridView = rootView.findViewById(R.id.gridView);
		gridViewAdapter = new GridViewAdapter(getActivity());
		gridView.setAdapter(gridViewAdapter);

		String url = getDefaultUrl();
		startAsyncTask(url);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cursor.moveToPosition(position);
				Movie movie = new Movie();
				movie.id = cursor.getInt(INDEX_MOVIE_MOVIE_ID);
				movie.releaseDate = cursor.getString(INDEX_MOVIE_RELEASE_DATE);
				movie.voteAverage = cursor.getString(INDEX_MOVIE_VOTE_AVERAGE);
				movie.posterPath = cursor.getString(INDEX_MOVIE_POSTER_PATH);
				movie.overview = cursor.getString(INDEX_MOVIE_OVERVIEW);
				movie.originalTitle = cursor.getString(INDEX_MOVIE_ORIGINAL_TITLE);
				getReviewsAndVideos(movie);
			}
		});

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

		switch (loaderId) {

			case ID_MOVIE_LOADER:
				Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
				return new CursorLoader(getContext(), movieQueryUri, MAIN_MOVIE_PROJECTION, null,
						null, null);

			default:
				throw new RuntimeException("Loader Not Implemented: " + loaderId);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		gridViewAdapter.swapCursor(data);
		cursor = data;
		if (position == RecyclerView.NO_POSITION) {
			position = 0;
		}
		gridView.smoothScrollToPosition(position);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		gridViewAdapter.swapCursor(null);
	}

	private void getReviewsAndVideos(final Movie movie) {
		videoDownloadCompleted = false;
		reviewDownloadCompleted = false;

		GetAsyncTask getReviewsAsyncTask = new GetAsyncTask(
				new GetAsyncTask.GetMoviesAsyncTaskDelegate() {
					@Override
					public void onPostExecute(String result) {
						reviewDownloadCompleted = true;
						ReviewList reviewList = GsonHelper.getGson().fromJson(result,
								ReviewList.class);
						if (reviewList != null && reviewList.results != null) {
							movie.reviews = reviewList.results;
						}
						handleExtraDownloadSuccess(movie);
					}
				});
		getReviewsAsyncTask.execute(UrlHelper.getReviews(movie.id));

		GetAsyncTask getVideosAsyncTask = new GetAsyncTask(
				new GetAsyncTask.GetMoviesAsyncTaskDelegate() {
					@Override
					public void onPostExecute(String result) {
						videoDownloadCompleted = true;
						VideoList videoList = GsonHelper.getGson().fromJson(result,
								VideoList.class);
						if (videoList != null && videoList.results != null) {
							movie.videos = videoList.results;
						}
						handleExtraDownloadSuccess(movie);
					}
				});
		getVideosAsyncTask.execute(UrlHelper.getVideos(movie.id));
	}

	private void handleExtraDownloadSuccess(Movie movie) {
		if (videoDownloadCompleted && reviewDownloadCompleted) {
			gridFragmentCallback.openDetailView(movie);
		}
	}

	public void update(String url) {
		startAsyncTask(url);
	}

	private void startAsyncTask(String url) {
		GetAsyncTask getAsyncTask = new GetAsyncTask(new GetAsyncTask.GetMoviesAsyncTaskDelegate() {
					@Override
					public void onPostExecute(String result) {
						MovieList movieList = GsonHelper.getGson().fromJson(result,
								MovieList.class);
						if (movieList == null || movieList.results == null) {
							Toast.makeText(getContext(), getString(R.string.failed_to_get_movies),
									Toast.LENGTH_SHORT).show();
							return;
						}
						insertMoviesDataToDb(movieList.results);
					}
				});
		getAsyncTask.execute(url);
	}

	private void insertMoviesDataToDb(List<Movie> results) {
		List<ContentValues> values = new ArrayList<>();
		for (Movie movie : results) {
			ContentValues movieValues = new ContentValues();
			movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
			movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
			movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
			movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
			movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
			movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.originalTitle);
			values.add(movieValues);
		}
		getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
				values.toArray(new ContentValues[values.size()]));

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof GridFragmentCallback) {
			gridFragmentCallback = (GridFragmentCallback) context;
		}
		getActivity().getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
	}

	interface GridFragmentCallback {

		void openDetailView(Movie movie);
	}
}

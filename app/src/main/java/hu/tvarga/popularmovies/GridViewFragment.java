package hu.tvarga.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hu.tvarga.popularmovies.dataaccess.Movie;
import hu.tvarga.popularmovies.dataaccess.MovieList;
import hu.tvarga.popularmovies.dataaccess.ReviewList;
import hu.tvarga.popularmovies.dataaccess.VideoList;
import hu.tvarga.popularmovies.dataaccess.database.MovieContract;
import hu.tvarga.popularmovies.utility.GsonHelper;
import hu.tvarga.popularmovies.utility.UrlHelper;

import static hu.tvarga.popularmovies.GridViewActivity.FAVORITE;
import static hu.tvarga.popularmovies.GridViewActivity.MULTI_PANE_EXTRA_KEY;
import static hu.tvarga.popularmovies.GridViewActivity.getSharedPreferences;
import static hu.tvarga.popularmovies.utility.UrlHelper.getDefaultUrl;

public class GridViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String MOVIE_EXTRA_KEY = "movie";
	static final String[] MAIN_MOVIE_PROJECTION =
			{MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_FAVORITE,};

	public static final int INDEX_MOVIE_MOVIE_ID = 0;
	public static final int INDEX_MOVIE_FAVORITE = 1;

	public static final int ID_MOVIE_LOADER = 44;
	public static final String FILTER_SHARED_PREFERENCES_KEY = "FILTER_SHARED_PREFERENCES_KEY";
	public static final String FIRST_VISIBLE_POSITION = "FIRST_VISIBLE_POSITION";

	private GridFragmentCallback gridFragmentCallback;
	private GridViewAdapter gridViewAdapter;
	private List<Movie> movies = new ArrayList<>();

	private boolean videoDownloadCompleted;
	private boolean reviewDownloadCompleted;
	private boolean favoritesOnly;
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
		gridViewAdapter = new GridViewAdapter(getActivity(), movies);
		gridView.setAdapter(gridViewAdapter);

		String url = getDefaultUrl();
		startAsyncTask(url);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Movie movie = movies.get(position);
				getReviewsAndVideos(movie);
			}
		});

		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

		if (loaderId == ID_MOVIE_LOADER) {
			Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
			return new CursorLoader(getContext(), movieQueryUri, MAIN_MOVIE_PROJECTION, null, null,
					null);
		}
		else {
			throw new LoaderNotImplementedExceptions(loaderId);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		handleDbChange(cursor);
	}

	private void handleDbChange(Cursor cursor) {
		SparseBooleanArray favoriteMap = new SparseBooleanArray();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(INDEX_MOVIE_MOVIE_ID);
			boolean favorite = cursor.getInt(INDEX_MOVIE_FAVORITE) != 0;
			favoriteMap.put(id, favorite);
		}

		for (Iterator<Movie> iterator = movies.iterator(); iterator.hasNext(); ) {
			Movie movie = iterator.next();
			movie.favorite = favoriteMap.get(movie.id);
			if (favoritesOnly && !movie.favorite) {
				iterator.remove();
			}
		}

		gridViewAdapter.notifyDataSetChanged();
		SharedPreferences sharedPreferences = getSharedPreferences(getContext());
		int firstVisibleItem = sharedPreferences.getInt(FIRST_VISIBLE_POSITION, 0);
		gridView.smoothScrollToPosition(firstVisibleItem);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// nothing to do
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
			Context context = getContext();
			if (context == null) {
				return;
			}
			Cursor query = context.getContentResolver().query(
					MovieContract.MovieEntry.CONTENT_URI.buildUpon()
							.appendPath(Integer.toString(movie.id)).build(), MAIN_MOVIE_PROJECTION,
					null, null, null);
			if (query != null && query.getCount() > 0) {
				movie.favorite = true;
				query.close();
			}
			gridFragmentCallback.openDetailView(movie);
		}
	}

	public void update(String url) {
		String urlToGet = url;
		if (FAVORITE.equals(url)) {
			favoritesOnly = true;
			// if we were properly caching everything we'd not need to still fetch from network
			urlToGet = UrlHelper.getUrlSortByPopularity();
		}
		else {
			favoritesOnly = false;
		}

		startAsyncTask(urlToGet);
	}

	private void startAsyncTask(String url) {
		GetAsyncTask getAsyncTask = new GetAsyncTask(new GetAsyncTask.GetMoviesAsyncTaskDelegate() {
			@Override
			public void onPostExecute(String result) {
				MovieList movieList = GsonHelper.getGson().fromJson(result, MovieList.class);
				if (movieList == null || movieList.results == null) {
					Toast.makeText(getContext(), getString(R.string.failed_to_get_movies),
							Toast.LENGTH_SHORT).show();
					return;
				}
				movies.clear();
				movies.addAll(movieList.results);
				Context context = getContext();
				if (context == null) {
					return;
				}
				Cursor query = context.getContentResolver().query(
						MovieContract.MovieEntry.CONTENT_URI, MAIN_MOVIE_PROJECTION, null, null,
						null);
				handleDbChange(query);
			}
		});
		getAsyncTask.execute(url);
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

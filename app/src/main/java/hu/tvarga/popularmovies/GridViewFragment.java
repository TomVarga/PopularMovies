package hu.tvarga.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import hu.tvarga.popularmovies.utility.GsonHelper;

import static hu.tvarga.popularmovies.GridViewActivity.MULTI_PANE_EXTRA_KEY;
import static hu.tvarga.popularmovies.utility.UrlHelper.getDefaultUrl;

public class GridViewFragment extends Fragment {

	private GridFragmentCallback gridFragmentCallback;
	private List<Movie> movies = new ArrayList<>();
	private GridViewAdapter gridViewAdapter;
	public static final String MOVIE_EXTRA_KEY = "movie";

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

		GridView gridView = rootView.findViewById(R.id.gridView);
		gridViewAdapter = new GridViewAdapter(getActivity(), movies);
		gridView.setAdapter(gridViewAdapter);

		String url = getDefaultUrl();
		startAsyncTask(url);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Movie movie = movies.get(position);
				gridFragmentCallback.openDetailView(movie);
			}
		});

		return rootView;
	}

	public void update(String url) {
		startAsyncTask(url);
	}

	private void startAsyncTask(String url) {
		GetMoviesAsyncTask getMoviesAsyncTask = new GetMoviesAsyncTask(
				new GetMoviesAsyncTask.GetMoviesAsyncTaskDelegate() {
					@Override
					public void onPostExecute(String result) {
						MovieList movieList = GsonHelper.getGson().fromJson(result,
								MovieList.class);
						if (movieList == null || movieList.results == null) {
							Toast.makeText(getContext(), getString(R.string.failed_to_get_movies),
									Toast.LENGTH_SHORT).show();
							return;
						}
						movies.clear();
						movies.addAll(movieList.results);
						gridViewAdapter.notifyDataSetChanged();
					}
				});
		getMoviesAsyncTask.execute(url);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof GridFragmentCallback) {
			gridFragmentCallback = (GridFragmentCallback) context;
		}
	}

	interface GridFragmentCallback {

		void openDetailView(Movie movie);
	}
}

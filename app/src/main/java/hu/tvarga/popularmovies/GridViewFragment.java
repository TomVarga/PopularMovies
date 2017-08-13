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

import java.util.ArrayList;
import java.util.List;

import hu.tvarga.popularmovies.dataaccess.Movie;
import hu.tvarga.popularmovies.dataaccess.MovieList;
import hu.tvarga.popularmovies.utility.GsonHelper;

import static hu.tvarga.popularmovies.GridViewActivity.MULTI_PANE_EXTRA_KEY;

public class GridViewFragment extends Fragment {

	private GridFragmentCallback gridFragmentCallback;
	private GridView gridView;
	private boolean multiPane;
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

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			multiPane = getArguments().getBoolean(MULTI_PANE_EXTRA_KEY);
		}
		else {
			multiPane = savedInstanceState.getBoolean(MULTI_PANE_EXTRA_KEY);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_grid_view, container, false);

		gridView = rootView.findViewById(R.id.gridView);
		gridViewAdapter = new GridViewAdapter(getActivity(), movies);
		gridView.setAdapter(gridViewAdapter);

		String url = BuildConfig.url_default + "api_key=" + BuildConfig.cfg_api_key;
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

	private void startAsyncTask(String url) {
		GetMoviesAsyncTask getMoviesAsyncTask = new GetMoviesAsyncTask(
				new GetMoviesAsyncTask.GetMoviesAsyncTaskDelegate() {
					@Override
					public void onPostExecute(String result) {
						MovieList movieList = GsonHelper.getGson().fromJson(result,
								MovieList.class);
						movies.clear();
						movies.addAll(movieList.results);
						gridViewAdapter.notifyDataSetInvalidated();
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

	public interface GridFragmentCallback {

		void openDetailView(Movie movie);
	}
}

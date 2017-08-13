package hu.tvarga.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import hu.tvarga.popularmovies.dataaccess.Movie;
import hu.tvarga.popularmovies.utility.UrlHelper;

import static hu.tvarga.popularmovies.GridViewFragment.MOVIE_EXTRA_KEY;

public class GridViewActivity extends AppCompatActivity
		implements GridViewFragment.GridFragmentCallback {

	public static final String MULTI_PANE_EXTRA_KEY = "multiPane";
	public static final String GRID_FRAGMENT_SAVED_INSTANCE_KEY = "gridFragment";

	private boolean multiPane;
	private GridViewFragment gridViewFragment;
	private DetailViewFragment detailViewFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);

		if (findViewById(R.id.movieDetailContainer) != null) {
			multiPane = true;
		}

		if (savedInstanceState != null) {
			gridViewFragment = (GridViewFragment) getSupportFragmentManager().getFragment(
					savedInstanceState, GRID_FRAGMENT_SAVED_INSTANCE_KEY);
		}

		if (gridViewFragment == null) {
			gridViewFragment = GridViewFragment.newInstance(multiPane);
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.movieGridContainer, gridViewFragment);
		fragmentTransaction.commit();

		if (multiPane) {
			detailViewFragment = new DetailViewFragment();
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.movieDetailContainer, detailViewFragment);
			transaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		String url = null;
		if (id == R.id.actionSortByPopularity) {
			url = UrlHelper.getUrlSortByPopularity();
		}
		else if (id == R.id.actionSortByRating) {
			url = UrlHelper.getUrlSortByRating();
		}
		gridViewFragment.update(url);

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState);
		getSupportFragmentManager().putFragment(outState, GRID_FRAGMENT_SAVED_INSTANCE_KEY,
				gridViewFragment);
	}

	@Override
	public void openDetailView(Movie movie) {
		if (multiPane) {
			detailViewFragment.updateMovie(movie);
		}
		else {
			Intent intent = new Intent(GridViewActivity.this, DetailViewActivity.class);
			intent.putExtra(MOVIE_EXTRA_KEY, movie);
			startActivity(intent);
		}
	}
}

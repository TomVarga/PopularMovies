package hu.tvarga.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import hu.tvarga.popularmovies.dataaccess.Movie;

import static hu.tvarga.popularmovies.GridViewFragment.MOVIE_EXTRA_KEY;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class DetailViewActivity extends AppCompatActivity {

	private DetailViewFragment detailViewFragment;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Movie movie = (Movie) intent.getSerializableExtra(MOVIE_EXTRA_KEY);

		setContentView(R.layout.activity_detail_view);

		ActionBar supportActionBar = getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}

		if (detailViewFragment == null) {
			detailViewFragment = DetailViewFragment.newInstance(movie);
		}
		else {
			detailViewFragment.updateMovie(movie);
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.movieDetailContainer, detailViewFragment);
		fragmentTransaction.commit();
	}
}

package hu.tvarga.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static hu.tvarga.popularmovies.utility.UrlHelper.getPosterUrl;

class GridViewAdapter extends BaseAdapter {

	private final Context context;
	private Cursor cursor;

	GridViewAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		if (null == cursor) {
			return 0;
		}
		return cursor.getCount();
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

		cursor.moveToPosition(i);
		ImageView imageView = viewItem.findViewById(R.id.moviePoster);
		Picasso.with(context).load(
				getPosterUrl(cursor.getString(GridViewFragment.INDEX_MOVIE_POSTER_PATH))).into(
				imageView);

		return viewItem;
	}

	public void swapCursor(Cursor data) {
		cursor = data;
		notifyDataSetChanged();
	}
}

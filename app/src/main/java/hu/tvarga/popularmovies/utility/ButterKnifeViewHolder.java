package hu.tvarga.popularmovies.utility;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ButterKnifeViewHolder extends RecyclerView.ViewHolder {

	public Unbinder unbinder;

	public ButterKnifeViewHolder(View itemView) {
		super(itemView);
		unbinder = ButterKnife.bind(this, itemView);
	}
}

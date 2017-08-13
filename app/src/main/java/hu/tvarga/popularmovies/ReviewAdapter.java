package hu.tvarga.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import hu.tvarga.popularmovies.dataaccess.Review;
import hu.tvarga.popularmovies.utility.ButterKnifeViewHolder;

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

	private final Context context;
	private final List<Review> reviews;

	ReviewAdapter(Context context, List<Review> reviews) {
		this.context = context;
		this.reviews = reviews;
	}

	@Override
	public void onBindViewHolder(ReviewViewHolder holder, int position) {
		holder.onBind(reviews.get(position));
	}

	@Override
	public int getItemCount() {
		return reviews == null ? 0 : reviews.size();
	}

	@Override
	public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ReviewViewHolder(
				LayoutInflater.from(context).inflate(R.layout.review_item, parent, false));
	}

	public class ReviewViewHolder extends ButterKnifeViewHolder {

		@BindView(R.id.author)
		TextView author;
		@BindView(R.id.content)
		TextView content;

		public ReviewViewHolder(View itemView) {
			super(itemView);
		}

		public void onBind(Review review) {
			author.setText(review.author);
			content.setText(review.content);
		}
	}
}

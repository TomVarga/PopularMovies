package hu.tvarga.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import hu.tvarga.popularmovies.dataaccess.Video;
import hu.tvarga.popularmovies.utility.ButterKnifeViewHolder;

class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

	private final Context context;
	private final List<Video> videos;

	VideoAdapter(Context context, List<Video> videos) {
		this.context = context;
		this.videos = videos;
	}

	@Override
	public void onBindViewHolder(VideoAdapter.VideoViewHolder holder, int position) {
		holder.onBind(videos.get(position));
	}

	@Override
	public int getItemCount() {
		return videos == null ? 0 : videos.size();
	}

	@Override
	public VideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new VideoAdapter.VideoViewHolder(
				LayoutInflater.from(context).inflate(R.layout.video_item, parent, false));
	}

	public class VideoViewHolder extends ButterKnifeViewHolder {

		@BindView(R.id.title)
		TextView title;

		public VideoViewHolder(View itemView) {
			super(itemView);
		}

		public void onBind(final Video video) {
			title.setText(video.name);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!"YouTube".equals(video.site)) {
						Toast.makeText(context, context.getString(R.string.only_youtube_supported),
								Toast.LENGTH_SHORT).show();
					}
					String youtubeLink = "http://www.youtube.com/watch?v=" + video.key;
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(youtubeLink));
					intent.setPackage("com.google.android.youtube");
					context.startActivity(intent);
				}
			});
		}
	}
}
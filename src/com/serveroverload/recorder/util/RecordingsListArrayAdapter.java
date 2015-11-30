package com.serveroverload.recorder.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.serveroverload.recorder.R;
import com.serveroverload.recorder.R.id;
import com.serveroverload.recorder.R.layout;
import com.serveroverload.recorder.customview.SwipeLayout;
import com.serveroverload.recorder.ui.HomeActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecordingsListArrayAdapter extends ArrayAdapter<String>

{
	public RecordingsListArrayAdapter(Context context, int resource,
			List<String> listOfRecordings) {
		super(context, resource, listOfRecordings);

		this.context = context;
		this.listOfRecordings = listOfRecordings;
	}

	private final Context context;
	private final List<String> listOfRecordings;
	private ViewHolder holder;

	private class ViewHolder {
		TextView recordingName;
		SwipeLayout currentSwipeLayout;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(
					R.layout.swipe_layout_recording_list_item, parent, false);

			holder = new ViewHolder();

			holder.recordingName = (TextView) convertView
					.findViewById(R.id.textViewRecordingName);

			holder.recordingName.setSelected(true);

			holder.currentSwipeLayout = (SwipeLayout) convertView
					.findViewById(R.id.swipe_layout_1);

			holder.currentSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

			holder.currentSwipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.recordingName.setText(((HomeActivity) context).getRecordings().get(position));

		// Tapped in hidden menu
		holder.currentSwipeLayout.findViewById(R.id.share).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent sharingIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						sharingIntent.setType("text/plain");
						File archivo = new File(((HomeActivity) context).getRecordings().get(position));
						sharingIntent.putExtra(Intent.EXTRA_STREAM,
								Uri.fromFile(archivo));
						context.startActivity(Intent.createChooser(
								sharingIntent, "Share via"));

					}
				});

		holder.currentSwipeLayout.findViewById(R.id.delete_song)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Helper.getHelperInstance().makeHepticFeedback(context);

						if (new File(((HomeActivity) context).getRecordings().get(position)).delete()) {
							Toast.makeText(getContext(), "Deleted", 500).show();

							((HomeActivity) context).getRecordings().remove(position);

							notifyDataSetChanged();

							((HomeActivity) context).getmMediaPlayer().reset();
						}

					}

				});

//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				MediaPlayer mMediaPlayer = ((HomeActivity) context)
//						.getmMediaPlayer();
//
//				try {
//
//					mMediaPlayer.reset();
//					mMediaPlayer.setDataSource(((HomeActivity) context).getRecordings().get(position));
//					mMediaPlayer.prepare();
//					mMediaPlayer.start();
//
//				} catch (IllegalArgumentException | SecurityException
//						| IllegalStateException | IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		});

		return convertView;
	}
}

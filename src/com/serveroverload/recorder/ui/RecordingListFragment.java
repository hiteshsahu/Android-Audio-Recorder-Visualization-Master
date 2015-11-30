/**
 * 
 */
package com.serveroverload.recorder.ui;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.serveroverload.recorder.R;
import com.serveroverload.recorder.customview.PlayerVisualizerView;
import com.serveroverload.recorder.util.Helper;
import com.serveroverload.recorder.util.RecordingsLoaderTask;

/**
 * @author Hitesh
 *
 */
public class RecordingListFragment extends Fragment implements
		OnRefreshListener {

	private static final float VISUALIZER_HEIGHT_DIP = 100f;
	private SwipeRefreshLayout swipeLayout;
	private ListView recordingsListView;
	private LinearLayout mLinearLayout;
	private com.serveroverload.recorder.customview.PlayerVisualizerView mVisualizerView;
	private Visualizer mVisualizer;
	private View rootView;
	private boolean SONGPAUSED;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.recording_list_fragment,
				container, false);

		((HomeActivity) getActivity()).RecordingNumber = 0;
		//
		// ((HomeActivity) getActivity()).getmActionBarTitle().setText(
		// getResources().getString(R.string.title_album));

		recordingsListView = (ListView) rootView
				.findViewById(R.id.listView_Recording);

		new RecordingsLoaderTask(swipeLayout, recordingsListView, getActivity())
				.execute(Helper.LOAD_RECORDINGS);

		recordingsListView.setFastScrollEnabled(true);

		// }

		swipeLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(RecordingListFragment.this);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		// listen for when the music stream ends playing
		((HomeActivity) getActivity()).getmMediaPlayer()
				.setOnCompletionListener(
						new MediaPlayer.OnCompletionListener() {
							public void onCompletion(MediaPlayer mediaPlayer) {
								// disable the visualizer as it's no longer
								// needed

								if (null != mVisualizer)
									mVisualizer.setEnabled(false);

								rootView.findViewById(R.id.btnPauseSlider)
										.setVisibility(View.GONE);

								rootView.findViewById(R.id.btnPlaySlider)
										.setVisibility(View.VISIBLE);

							}
						});

		mLinearLayout = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutVisual);
		// Create a VisualizerView to display the audio waveform for the current
		// settings
		mVisualizerView = new PlayerVisualizerView(getActivity());
		mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (VISUALIZER_HEIGHT_DIP * getResources()
						.getDisplayMetrics().density)));
		mLinearLayout.addView(mVisualizerView);

		// Create the Visualizer object and attach it to our media player.
		mVisualizer = new Visualizer(((HomeActivity) getActivity())
				.getmMediaPlayer().getAudioSessionId());

		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

		mVisualizer.setDataCaptureListener(
				new Visualizer.OnDataCaptureListener() {
					public void onWaveFormDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
						mVisualizerView.updateVisualizer(bytes);
					}

					public void onFftDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
					}
				}, Visualizer.getMaxCaptureRate() / 2, true, false);

		mVisualizer.setEnabled(true);

		rootView.findViewById(R.id.btnPauseSlider).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());
						
						if (null != mVisualizer)
							mVisualizer.setEnabled(false);

						((HomeActivity) getActivity()).getmMediaPlayer()
								.pause();

						SONGPAUSED = true;

						rootView.findViewById(R.id.btnPauseSlider)
								.setVisibility(View.GONE);

						rootView.findViewById(R.id.btnPlaySlider)
								.setVisibility(View.VISIBLE);

					}
				});

		rootView.findViewById(R.id.btnPlaySlider).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());

						if (SONGPAUSED) {

							resumeSong();

						} else {

							playSong(((HomeActivity) getActivity()).RecordingNumber);

						}
					}
				});

		rootView.findViewById(R.id.btnNextSlider).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());

						if (((HomeActivity) getActivity()).getRecordings()
								.size() > 0) {
							if (((HomeActivity) getActivity()).RecordingNumber < (((HomeActivity) getActivity())
									.getRecordings().size() - 1)) {
								((HomeActivity) getActivity()).RecordingNumber++;
							} else {
								((HomeActivity) getActivity()).RecordingNumber = 0;
							}

							playSong(((HomeActivity) getActivity()).RecordingNumber);
						}

					}
				});

		rootView.findViewById(R.id.btnBackSlider).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());

						if (((HomeActivity) getActivity()).getRecordings()
								.size() > 0) {
							if (((HomeActivity) getActivity()).RecordingNumber > 0) {
								((HomeActivity) getActivity()).RecordingNumber--;
							} else {
								((HomeActivity) getActivity()).RecordingNumber = ((HomeActivity) getActivity())
										.getRecordings().size() - 1;
							}

							playSong(((HomeActivity) getActivity()).RecordingNumber);
						}

					}
				});

		rootView.findViewById(R.id.btnNextSlider).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (((HomeActivity) getActivity()).getRecordings()
								.size() > 0) {
							if (((HomeActivity) getActivity()).RecordingNumber < (((HomeActivity) getActivity())
									.getRecordings().size() - 1)) {
								((HomeActivity) getActivity()).RecordingNumber++;
							} else {
								((HomeActivity) getActivity()).RecordingNumber = 0;
							}
						}
						playSong(((HomeActivity) getActivity()).RecordingNumber);

					}
				});

		recordingsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				((HomeActivity) getActivity()).RecordingNumber = position;

				playSong(position);

			}
		});

		return rootView;
	}

	@Override
	public void onRefresh() {

		new RecordingsLoaderTask(swipeLayout, recordingsListView, getActivity())
				.execute(Helper.LOAD_RECORDINGS);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (null != mVisualizer)
			mVisualizer.release();
		mVisualizer = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (null != mVisualizer)
			mVisualizer.release();
		mVisualizer = null;

		((HomeActivity) getActivity()).getmMediaPlayer().reset();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		// activityReference = activity;
	}

	private void playSong(int RecordingNumber) {
		MediaPlayer mMediaPlayer = ((HomeActivity) getActivity())
				.getmMediaPlayer();
		
		if (null != mVisualizer)
			mVisualizer.setEnabled(true);

		if (null != mMediaPlayer
				&& !((HomeActivity) getActivity()).getRecordings().isEmpty()) {
			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(((HomeActivity) getActivity())
						.getRecordings().get(RecordingNumber).toString());

				mMediaPlayer.prepare();
				mMediaPlayer.start();

				rootView.findViewById(R.id.btnPauseSlider).setVisibility(
						View.VISIBLE);

				rootView.findViewById(R.id.btnPlaySlider).setVisibility(
						View.GONE);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void resumeSong() {
		
		if (null != mVisualizer)
			mVisualizer.setEnabled(true);
		
		((HomeActivity) getActivity()).getmMediaPlayer().start();

		rootView.findViewById(R.id.btnPauseSlider).setVisibility(View.VISIBLE);

		rootView.findViewById(R.id.btnPlaySlider).setVisibility(View.GONE);

		SONGPAUSED = false;
	}

	// private Activity activityReference;

}

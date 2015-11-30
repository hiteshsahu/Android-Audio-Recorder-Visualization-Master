package com.serveroverload.recorder.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.serveroverload.recorder.R;
import com.serveroverload.recorder.customview.RecorderVisualizerView;
import com.serveroverload.recorder.util.Helper;

public class RecordAudioFragment extends Fragment {

	private String currentOutFile;
	private MediaRecorder myAudioRecorder;

	private boolean isRecording;
	private RecorderVisualizerView visualizerView;
	private View rootView;

	private boolean doubleBackToExitPressedOnce;
	private Handler mHandler = new Handler();

	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			doubleBackToExitPressedOnce = false;
		}
	};

	public static final int REPEAT_INTERVAL = 40;

	private Handler handler = new Handler(); // Handler for updating the
												// visualizer

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.record_audio_fragment, container,
				false);

		rootView.findViewById(R.id.stop_recording).setEnabled(false);
		rootView.findViewById(R.id.delete_recording).setEnabled(false);

		visualizerView = (RecorderVisualizerView) rootView
				.findViewById(R.id.visualizer);

		rootView.findViewById(R.id.start_recording).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());

						if (Helper.getHelperInstance().createRecordingFolder()) {

							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyyMMdd_HH_mm_ss");
							String currentTimeStamp = dateFormat
									.format(new Date());

							currentOutFile = Helper.RECORDING_PATH
									+ "/recording_" + currentTimeStamp + ".3gp";

							myAudioRecorder = new MediaRecorder();
							myAudioRecorder
									.setAudioSource(MediaRecorder.AudioSource.MIC);
							myAudioRecorder
									.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
							myAudioRecorder
									.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
							myAudioRecorder.setOutputFile(currentOutFile);

							try {

								myAudioRecorder.prepare();
								myAudioRecorder.start();

								Toast.makeText(
										getActivity(),
										getActivity().getResources().getString(
												R.string.rec_start),
										Toast.LENGTH_LONG).show();

								rootView.findViewById(R.id.start_recording)
										.setEnabled(false);
								rootView.findViewById(R.id.stop_recording)
										.setEnabled(true);
								rootView.findViewById(R.id.delete_recording)
										.setEnabled(false);

								isRecording = true;

								handler.post(updateVisualizer);
							}

							catch (IllegalStateException e) {
								Toast.makeText(
										getActivity(),
										getActivity().getResources().getString(
												R.string.rec_fail),
										Toast.LENGTH_LONG).show();
								e.printStackTrace();

								rootView.findViewById(R.id.start_recording)
										.setEnabled(true);
								rootView.findViewById(R.id.stop_recording)
										.setEnabled(false);
								rootView.findViewById(R.id.delete_recording)
										.setEnabled(true);

								isRecording = false;
							}

							catch (IOException e) {
								Toast.makeText(
										getActivity(),
										getActivity().getResources().getString(
												R.string.rec_fail),
										Toast.LENGTH_LONG).show();
								e.printStackTrace();

								rootView.findViewById(R.id.start_recording)
										.setEnabled(true);
								rootView.findViewById(R.id.stop_recording)
										.setEnabled(false);
								rootView.findViewById(R.id.delete_recording)
										.setEnabled(true);

								isRecording = false;
							}
						} else {

							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.rec_fail_mkdir),
									Toast.LENGTH_LONG).show();

							isRecording = false;
						}

						return false;
					}
				});

		rootView.findViewById(R.id.stop_recording).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());
						try {

							if (null != myAudioRecorder) {
								myAudioRecorder.stop();
								myAudioRecorder.release();
								myAudioRecorder = null;

								Toast.makeText(
										getActivity(),
										getActivity().getResources().getString(
												R.string.rec_saved)
												+ currentOutFile,
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.rec_fail),
									Toast.LENGTH_LONG).show();
						}

						rootView.findViewById(R.id.start_recording).setEnabled(
								true);
						rootView.findViewById(R.id.stop_recording).setEnabled(
								false);
						rootView.findViewById(R.id.delete_recording)
								.setEnabled(true);

						isRecording = false;

						handler.removeCallbacks(updateVisualizer);

						return false;
					}
				});

		rootView.findViewById(R.id.delete_recording).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						File recording = new File(currentOutFile);

						if (recording.exists() && recording.delete()) {
							Toast.makeText(
									getActivity(),
									getResources().getString(
											R.string.rec_deleted)
											+ currentOutFile,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.rec_delete_fail)
											+ currentOutFile,
									Toast.LENGTH_SHORT).show();
						}

						rootView.findViewById(R.id.stop_recording).setEnabled(
								false);
						rootView.findViewById(R.id.delete_recording)
								.setEnabled(false);
						return false;
					}
				});

		rootView.findViewById(R.id.txt_cancel).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						Helper.getHelperInstance().makeHepticFeedback(
								getActivity());

						try {

							if (null != myAudioRecorder) {
								myAudioRecorder.stop();
								myAudioRecorder.release();
								myAudioRecorder = null;

								Toast.makeText(
										getActivity(),
										getActivity().getResources().getString(
												R.string.rec_saved)
												+ currentOutFile,
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.rec_fail),
									Toast.LENGTH_LONG).show();
						}

						isRecording = false;

						handler.removeCallbacks(updateVisualizer);

						System.exit(0);

						return false;
					}
				});

		rootView.findViewById(R.id.browse_recording).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						FragmentManager fragmentManager = getActivity()
								.getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.replace(R.id.container,
								new RecordingListFragment());
						fragmentTransaction
								.addToBackStack("RecordingListFragment");
						fragmentTransaction.commit();

						return false;
					}
				});

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		rootView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					if (doubleBackToExitPressedOnce) {
						// super.onBackPressed();

						if (mHandler != null) {
							mHandler.removeCallbacks(mRunnable);
						}

						getActivity().finish();

						return true;
					}

					doubleBackToExitPressedOnce = true;
					Toast.makeText(getActivity(),
							"Please click BACK again to exit",
							Toast.LENGTH_SHORT).show();

					mHandler.postDelayed(mRunnable, 2000);

				}
				return true;
			}
		});

		return rootView;

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (isRecording) {

			try {

				if (null != myAudioRecorder) {

					myAudioRecorder.stop();
					myAudioRecorder.release();
					myAudioRecorder = null;

					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.rec_saved)
									+ currentOutFile, Toast.LENGTH_SHORT)
							.show();

					rootView.findViewById(R.id.start_recording)
							.setEnabled(true);
					rootView.findViewById(R.id.stop_recording)
							.setEnabled(false);
					rootView.findViewById(R.id.delete_recording).setEnabled(
							true);

					handler.removeCallbacks(updateVisualizer);
				}

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(
						getActivity(),
						getActivity().getResources().getString(
								R.string.rec_fail), Toast.LENGTH_LONG).show();

				rootView.findViewById(R.id.start_recording).setEnabled(true);
				rootView.findViewById(R.id.stop_recording).setEnabled(false);
				rootView.findViewById(R.id.delete_recording).setEnabled(true);

				handler.removeCallbacks(updateVisualizer);

			}
		}
	}

	// updates the visualizer every 50 milliseconds
	Runnable updateVisualizer = new Runnable() {
		@Override
		public void run() {
			if (isRecording) // if we are already recording
			{
				// get the current amplitude
				int x = myAudioRecorder.getMaxAmplitude();
				visualizerView.addAmplitude(x); // update the VisualizeView
				visualizerView.invalidate(); // refresh the VisualizerView

				// update in 40 milliseconds
				handler.postDelayed(this, REPEAT_INTERVAL);
			}
		}
	};

}

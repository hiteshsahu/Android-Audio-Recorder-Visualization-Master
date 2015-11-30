package com.serveroverload.recorder.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.view.View;

/**
 * code taken from official sample app see
 * ApiDemos>src>com>example>android>apis>media>AudioFxDemo.java A simple class
 * that draws waveform data received from a
 * {@link OnDataCaptureListener#onWaveFormDataCapture }
 */
public class PlayerVisualizerView extends View {
	private byte[] mBytes;
	private float[] mPoints;
	private Rect mRect = new Rect();

	private Paint mForePaint = new Paint();

	public PlayerVisualizerView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mBytes = null;

		mForePaint.setStrokeWidth(1f);
		mForePaint.setAntiAlias(true);
		mForePaint.setColor(Color.GREEN);
	}

	public void updateVisualizer(byte[] bytes) {
		mBytes = bytes;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBytes == null) {
			return;
		}

		if (mPoints == null || mPoints.length < mBytes.length * 4) {
			mPoints = new float[mBytes.length * 4];
		}

		mRect.set(0, 0, getWidth(), getHeight());

		for (int i = 0; i < mBytes.length - 1; i++) {
			mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
			mPoints[i * 4 + 1] = mRect.height() / 2
					+ ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
			mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
			mPoints[i * 4 + 3] = mRect.height() / 2
					+ ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
					/ 128;
		}

		canvas.drawLines(mPoints, mForePaint);
	}
}
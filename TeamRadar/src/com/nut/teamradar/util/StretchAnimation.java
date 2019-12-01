package com.nut.teamradar.util;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class StretchAnimation {

	private final static String TAG = "SizeChange";
	private Interpolator mInterpolator; 
	private View mView;    
	private int mCurrSize; 
	private int mRawSize;
	private int mMinSize; 
	private int mMaxSize; 
	private boolean isFinished = true;
	private TYPE mType = TYPE.vertical;
	private final static int FRAMTIME = 20;
	public static enum TYPE {
		horizontal, 
		vertical    
	}

	private int mDuration;  
	private long mStartTime;
	private float mDurationReciprocal; 
	private int mDSize; 

	public StretchAnimation(int maxSize, int minSize, TYPE type, int duration) {
		if (minSize >= maxSize) {
			throw new RuntimeException("View MinSize >= MaxSize");
		}
		mMinSize = minSize;
		mMaxSize = maxSize;
		mType = type;
		mDuration = duration;
	}

	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	public TYPE getmType() {
		return mType;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	private void changeViewSize() {

		if (mView != null && mView.getVisibility() != View.GONE) {
			LayoutParams params = mView.getLayoutParams();
			if (mType == TYPE.vertical) {
				params.height = mCurrSize;
			} else if (mType == TYPE.horizontal) {
				params.width = mCurrSize;
			}
			/*Log.i(TAG, "CurrSize = " + mCurrSize + " Max=" + mMaxSize + " min="
					+ mMinSize);*/
			mView.setLayoutParams(params);
			mView.setBackgroundColor(Color.GRAY);
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {
				if (!computeViewSize()) {

					mHandler.sendEmptyMessageDelayed(1, FRAMTIME);
				} else {
					if (animationlistener != null) {
						animationlistener.animationEnd(mView);
					}
				}
			}
			super.handleMessage(msg);
		}

	};

	private boolean computeViewSize() {

		if (isFinished) {
			return isFinished;
		}
		int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - mStartTime);

		if (timePassed <= mDuration) {
			float x = timePassed * mDurationReciprocal;
			if (mInterpolator != null) {
				x = mInterpolator.getInterpolation(x);
			}
			//mCurrSize = mRawSize + Math.round(x * mDSize);
			mCurrSize = mRawSize + mDSize;
		} else {

			isFinished = true;
			mCurrSize = mRawSize + mDSize;

		}
		changeViewSize();
		return isFinished;
	}

	public void startAnimation(View view) {

		if (view != null) {
			mView = view;
		} else {
			Log.e(TAG, "view NULL");
			return;
		}
		//LayoutParams params = mView.getLayoutParams();

		if (isFinished) {
			mDurationReciprocal = 1.0f / (float) mDuration;
			if (mType == TYPE.vertical) {
				mRawSize = mCurrSize = mView.getHeight();
			} else if (mType == TYPE.horizontal) {
				mRawSize = mCurrSize = mView.getWidth();
			}
			Log.i(TAG, "mRawSize=" + mRawSize);
			if (mCurrSize > mMaxSize || mCurrSize < mMinSize) {
				throw new RuntimeException(
						"View currentViewSize > mMaxSize || currentViewSize < mMinSize");
			}
			isFinished = false;
			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			if (mCurrSize < mMaxSize) {
				mDSize = mMaxSize - mCurrSize;
			} else {
				mDSize = mMinSize - mMaxSize;
			}
			Log.i(TAG, "mDSize=" + mDSize);
			mHandler.sendEmptyMessage(1);
		}
	}

	private AnimationListener animationlistener;

	public interface AnimationListener {
		public void animationEnd(View v);
	}

	public void setOnAnimationListener(AnimationListener listener) {
		animationlistener = listener;
	}
}
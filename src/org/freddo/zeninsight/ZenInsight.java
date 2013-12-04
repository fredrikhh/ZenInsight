package org.freddo.zeninsight;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ZenInsight extends Activity {
    //defining states
    private static int[] sCamSteps = { -1, 1, 0, 1, 0, 1, -1 };
    private static int[] sStringSteps = { R.string.step0text, R.string.step1text, R.string.step2text, R.string.step3text, R.string.step4text, R.string.step5text, R.string.step6text };

    //Keeping track of which camera is currently used
    private int mCurrentCamId = sCamSteps[0];
    private Camera mCurrentCam = null;
    private int mCurrentStep = 0;
    private FrameLayout mViewfinder = null;
    private TextView mText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zen_insight);

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        
        mViewfinder = (FrameLayout) contentView.findViewById(R.id.viewfinder);
        mText = (TextView) contentView.findViewById(R.id.lead_text);

        View prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switchStep(mCurrentStep-1);
			}
		});
        
        View nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switchStep(mCurrentStep+1);
			}
		});        
    }

    private Camera attachCamera(FrameLayout holder, int camera) {
    	if (camera == 0 || camera == 1) {
	    	try {
		        Camera cam = Camera.open(camera);
		        cam.setDisplayOrientation(90);
		        final CameraSurfaceView surface = new CameraSurfaceView(this, cam);
		        surface.setTag(mViewfinder);
		        holder.addView(surface);
		        return cam;
		    } catch (Exception e) {
		    	finish();
		    }
    	} else {
    		ImageView view = new ImageView(this);
    		view.setImageResource(R.drawable.enzo);
    		view.setTag(mViewfinder);
    		holder.addView(view);
    	}
    	return null;
    }
    
    private void releaseCamera() {
    	if (mCurrentCam != null) {
			mCurrentCam.stopPreview();
			mCurrentCam.release();
    	}
		View surface = mViewfinder.findViewWithTag(mViewfinder);
		mViewfinder.removeView(surface);
    }

    public void onPause() {
    	releaseCamera();
    	super.onPause();
    }

    public void onResume() {
    	super.onResume();
    	mCurrentCam = attachCamera(mViewfinder, mCurrentCamId);
    }
    
    private void switchStep(int newStep) {
    	if (newStep < sCamSteps.length && newStep > -1) {
    		mText.setText(sStringSteps[newStep]);
    		int newCamId = sCamSteps[newStep];

    		if (mCurrentCamId != newCamId) {
    			releaseCamera();
				mCurrentCamId = newCamId;
				mCurrentCam = attachCamera(mViewfinder, mCurrentCamId);
			}
			mCurrentStep = newStep;
    	}
    }
}

package com.asiawaters.fta;

import android.app.Activity;
import android.os.Bundle;

import com.asiawaters.fta.classes.TouchImageView;


public class TouchImageViewActivity extends Activity {
	
	private TouchImageView image;
	private FTA FTA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touchimageview);
		FTA = ((com.asiawaters.fta.FTA) getApplication());
		image = (TouchImageView) findViewById(R.id.img);
		image.setImageBitmap(FTA.getImageToShow());
	}
}

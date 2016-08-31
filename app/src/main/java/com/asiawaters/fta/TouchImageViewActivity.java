package com.asiawaters.fta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.asiawaters.fta.classes.TouchImageView;


public class TouchImageViewActivity  extends AppCompatActivity {
	
	private TouchImageView image;
	private FTA FTA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touchimageview);
		FTA = ((com.asiawaters.fta.FTA) getApplication());
		image = (TouchImageView) findViewById(R.id.img);
		image.setImageBitmap(FTA.getImageToShow());
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_icon);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}
	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_image, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.rotateRight) {
			FTA.setImageToShow(RotateBitmap(FTA.getImageToShow(), 90));
			image.setImageBitmap(FTA.getImageToShow());
			return true;
		}
		if (id == R.id.rotateLeft) {
			FTA.setImageToShow(RotateBitmap(FTA.getImageToShow(), -90));
			image.setImageBitmap(FTA.getImageToShow());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

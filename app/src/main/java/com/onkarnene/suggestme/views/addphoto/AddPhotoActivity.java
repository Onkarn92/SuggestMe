package com.onkarnene.suggestme.views.addphoto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.databinding.ActivityAddPhotoBinding;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Pair;
import com.onkarnene.suggestme.views.suggestpair.SuggestPairActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPhotoActivity extends AppCompatActivity {
	
	private final int REQUEST_IMAGE_CAPTURE = 1;
	private final int REQUEST_PICK_IMAGE = 2;
	private Context m_context;
	private String m_defaultPath;
	private boolean m_isShirt;
	private BottomSheetDialog m_sheetDialog;
	private File m_shirtImagePath;
	private File m_pantImagePath;
	private String m_sourceIntent;
	private ActivityAddPhotoBinding m_binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_binding = ActivityAddPhotoBinding.inflate(getLayoutInflater());
		setContentView(m_binding.getRoot());
		this.m_context = this;
		this.m_setupUI();
		m_binding.selectShirtBtn.setOnClickListener(this::selectShirtBtnClickHandler);
		m_binding.selectPantBtn.setOnClickListener(this::selectPantBtnClickHandler);
		m_binding.addPairBtn.setOnClickListener(this::addPair);
		m_binding.doneBtn.setOnClickListener(this::m_doneBtnClickHandler);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
			Bitmap image = (Bitmap) data.getExtras()
			                            .get("data");
			if (this.m_isShirt) {
				m_binding.selectShirtImage.setImageBitmap(image);
			} else {
				m_binding.selectPantImage.setImageBitmap(image);
			}
		} else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			if (this.m_isShirt) {
				m_binding.selectShirtImage.setImageURI(selectedImage);
			} else {
				m_binding.selectPantImage.setImageURI(selectedImage);
			}
		}
		this.m_createImageFile();
		m_binding.addPairBtn.setEnabled(true);
	}
	
	public void addPair(View view) {
		new AddPhotoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void m_doneBtnClickHandler(View view) {
		if (this.m_sourceIntent.equalsIgnoreCase(AppModel.FROM_LOGIN)) {
			startActivity(new Intent(this, SuggestPairActivity.class));
		}
		finish();
	}
	
	private void selectShirtBtnClickHandler(View view) {
		this.m_isShirt = true;
		this.m_sheetDialog.show();
	}
	
	private void selectPantBtnClickHandler(View view) {
		this.m_isShirt = false;
		this.m_sheetDialog.show();
	}
	
	/**
	 * Initialize UI components
	 */
	private void m_setupUI() {
		m_binding.addPairBtn.setEnabled(false);
		this.m_defaultPath = "android.resource://" + getPackageName() + "/" + R.drawable.place_holder;
		View view = getLayoutInflater().inflate(R.layout.bottom_sheet_item, null);
		this.m_sheetDialog = new BottomSheetDialog(this);
		this.m_sheetDialog.setCancelable(true);
		this.m_sheetDialog.setCanceledOnTouchOutside(true);
		this.m_sheetDialog.setContentView(view);
		this.m_shirtImagePath = new File(this.m_defaultPath);
		this.m_pantImagePath = new File(this.m_defaultPath);
		
		this.m_sourceIntent = getIntent().getStringExtra(AppModel.KEY_SOURCE);
		(view.findViewById(R.id.cameraIcon)).setOnClickListener(v -> {
			this.m_sheetDialog.dismiss();
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (cameraIntent.resolveActivity(getPackageManager()) != null) {
				startActivityForResult(cameraIntent, this.REQUEST_IMAGE_CAPTURE);
			}
		});
		(view.findViewById(R.id.galleryIcon)).setOnClickListener(v -> {
			this.m_sheetDialog.dismiss();
			Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(galleryIntent, this.REQUEST_PICK_IMAGE);
		});
		m_binding.addPhotoToolbar.setNavigationOnClickListener(v -> finish());
	}
	
	/**
	 * Create image file path
	 */
	private void m_createImageFile() {
		try {
			String currentTime = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
			String imageFileName = AppModel.customer.getCustomerID() + "_" + currentTime;
			File storageDir = new File(Environment.getExternalStorageDirectory()
			                                      .getAbsolutePath() + File.separator + getPackageName() + File.separator
			                           + AppModel.customer.getCustomerID() + File.separator);
			if (!storageDir.exists()) {
				storageDir.mkdirs();
			}
			File file = File.createTempFile(imageFileName, ".jpg", storageDir);
			if (this.m_isShirt) {
				this.m_shirtImagePath = file;
			} else {
				this.m_pantImagePath = file;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Show SnackBar on current view
	 */
	private void m_showSnackBar(String msg, int textColor) {
		Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
		snackbar.getView()
		        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.editTextBgColor, null));
		((TextView) snackbar.getView()
		                    .findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
		snackbar.show();
	}
	
	/**
	 * Save image in local storage
	 */
	private void m_saveShirtImage() {
		try {
			FileOutputStream outStream = new FileOutputStream(this.m_shirtImagePath);
			Bitmap bitmap = ((BitmapDrawable) m_binding.selectShirtImage.getDrawable()).getBitmap();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save image in local storage
	 */
	private void m_savePantImage() {
		try {
			FileOutputStream outStream = new FileOutputStream(this.m_pantImagePath);
			Bitmap bitmap = ((BitmapDrawable) m_binding.selectPantImage.getDrawable()).getBitmap();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Background task to save pair in database
	 */
	private class AddPhotoAsyncTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog m_dialog;
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (m_shirtImagePath.getAbsolutePath()
				                    .equalsIgnoreCase(m_defaultPath) || m_pantImagePath.getAbsolutePath()
				                                                                       .equalsIgnoreCase(m_defaultPath)) {
					m_showSnackBar(getResources().getString(R.string.select_pair_confirmation), Color.WHITE);
				} else {
					if (m_shirtImagePath != null && m_pantImagePath != null) {
						m_saveShirtImage();
						m_savePantImage();
						Pair pair = new Pair();
						pair.setCustomerID(AppModel.customer.getCustomerID());
						pair.setShirtPath(m_shirtImagePath.getAbsolutePath());
						pair.setPantPath(m_pantImagePath.getAbsolutePath());
						DatabaseHelper.getInstance(m_context)
						              .savePair(pair);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.m_dialog = new ProgressDialog(m_context);
			this.m_dialog.setCancelable(true);
			this.m_dialog.setMessage(getResources().getString(R.string.please_wait));
			this.m_dialog.setProgressStyle(R.style.AppCompatAlertDialogStyle);
			this.m_dialog.show();
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			this.m_dialog.dismiss();
			m_binding.addPairBtn.setEnabled(false);
			m_showSnackBar(getResources().getString(R.string.added_success), Color.WHITE);
		}
	}
}
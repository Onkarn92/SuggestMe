package com.onkarnene.suggestme.views.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.databinding.ActivityLoginBinding;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Customer;
import com.onkarnene.suggestme.views.addphoto.AddPhotoActivity;
import com.onkarnene.suggestme.views.signup.SignUpActivity;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	
	private final int REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE = 2;
	private ActivityLoginBinding m_binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(m_binding.getRoot());
		this.m_requestDatabasePermission();
		m_binding.signInBtn.setOnClickListener(this::signInClickHandler);
		m_binding.newUserText.setOnClickListener(this::newUserClickHandler);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					break;
				} else {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
					                                                     Manifest.permission.READ_EXTERNAL_STORAGE},
					                                  REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
				}
			}
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	
	public void newUserClickHandler(View view) {
		startActivity(new Intent(this, SignUpActivity.class));
	}
	
	private void signInClickHandler(View view) {
		String user = m_binding.usernameEdit.getText()
		                                    .toString();
		String pass = m_binding.passwordEdit.getText()
		                                    .toString();
		if (!TextUtils.isEmpty(user.trim()) && !TextUtils.isEmpty(pass.trim())) {
			Customer customer = DatabaseHelper.getInstance(this)
			                                  .authenticateCustomer(user.trim(), pass.trim());
			if (customer != null) {
				m_binding.usernameEdit.getText()
				                      .clear();
				m_binding.passwordEdit.getText()
				                      .clear();
				AppModel.customer = customer;
				SharedPreferences.Editor editor = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE).edit();
				editor.putInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
				editor.apply();
				Intent loginIntent = new Intent(LoginActivity.this, AddPhotoActivity.class);
				loginIntent.putExtra(AppModel.KEY_SOURCE, AppModel.FROM_LOGIN);
				startActivity(loginIntent);
				finish();
			} else {
				this.m_showSnackBar(getResources().getString(R.string.authentication_failed),
				                    ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
			}
		} else {
			this.m_showSnackBar(getResources().getString(R.string.all_fields_mandatory), Color.WHITE);
		}
	}
	
	/**
	 * Check for Database permissions to be granted if OS Version greater than lollipop
	 */
	private void m_requestDatabasePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(LoginActivity.this,
				                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
				                                               Manifest.permission.READ_EXTERNAL_STORAGE},
				                                  REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
			} else {
				ActivityCompat.requestPermissions(LoginActivity.this,
				                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
				                                               Manifest.permission.READ_EXTERNAL_STORAGE},
				                                  REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
			}
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
}
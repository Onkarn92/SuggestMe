package com.onkarnene.suggestme.views.signup;

import android.Manifest;
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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.databinding.ActivitySignUpBinding;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.Customer;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity
		implements DatePickerDialog.OnDateSetListener, ActivityCompat.OnRequestPermissionsResultCallback {
	
	private final int REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE = 2;
	private ActivitySignUpBinding m_binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_binding = ActivitySignUpBinding.inflate(getLayoutInflater());
		setContentView(m_binding.getRoot());
		m_binding.signUpToolbar.setNavigationOnClickListener(v -> finish());
		this.m_requestDatabasePermission();
		m_binding.datePickerImage.setOnClickListener(this::showDatePicker);
		m_binding.signUpBtn.setOnClickListener(this::signUpClickHandler);
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
	
	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
		m_binding.dobText.setText(date);
	}
	
	private void signUpClickHandler(View view) {
		if (this.m_validateUserInput()) {
			try {
				Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(m_binding.dobText.getText()
				                                                                                           .toString());
				String cID = m_binding.firstNameEdit.getText()
				                                    .toString()
				                                    .toLowerCase() + DateFormat.format("dd", date)
				                                                               .toString();
				Customer customer = new Customer();
				customer.setCustomerID(cID.toLowerCase());
				customer.setFirstName(m_binding.firstNameEdit.getText()
				                                             .toString()
				                                             .toLowerCase());
				customer.setSurname(m_binding.surnameEdit.getText()
				                                         .toString()
				                                         .toLowerCase());
				customer.setEmail(m_binding.emailEdit.getText()
				                                     .toString()
				                                     .toLowerCase());
				customer.setPassword(m_binding.passwordEdit.getText()
				                                           .toString()
				                                           .toLowerCase());
				customer.setMobileNumber(m_binding.mobileNumberEdit.getText()
				                                                   .toString()
				                                                   .toLowerCase());
				customer.setDob(m_binding.dobText.getText()
				                                 .toString()
				                                 .toLowerCase());
				DatabaseHelper.getInstance(this)
				              .saveCustomer(customer);
				this.m_showSnackBar(getResources().getString(R.string.register_success), Color.WHITE, true);
				this.m_setDefault();
				view.setEnabled(false);
			} catch (ParseException e) {
				e.printStackTrace();
				view.setEnabled(true);
				this.m_showSnackBar(getResources().getString(R.string.something_went_wrong), Color.RED, false);
			}
		} else {
			view.setEnabled(true);
			this.m_showSnackBar(getResources().getString(R.string.all_fields_mandatory), Color.WHITE, false);
		}
	}
	
	private void showDatePicker(View view) {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog dialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
		                                                       calendar.get(Calendar.DAY_OF_MONTH));
		dialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.colorOrange, null));
		dialog.setVersion(DatePickerDialog.Version.VERSION_2);
		dialog.show(getFragmentManager(), "DatePickerDialog");
	}
	
	/**
	 * Check for Database permissions to be granted if OS Version greater than lollipop
	 */
	private void m_requestDatabasePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(SignUpActivity.this,
				                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
				                                               Manifest.permission.READ_EXTERNAL_STORAGE},
				                                  REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
			} else {
				ActivityCompat.requestPermissions(SignUpActivity.this,
				                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
				                                               Manifest.permission.READ_EXTERNAL_STORAGE},
				                                  REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
			}
		}
	}
	
	/**
	 * @return false if all fields are empty otherwise true
	 */
	private boolean m_validateUserInput() {
		return (!TextUtils.isEmpty(m_binding.firstNameEdit.getText()
		                                                  .toString()
		                                                  .trim()) && !TextUtils.isEmpty(m_binding.surnameEdit.getText()
		                                                                                                      .toString()
		                                                                                                      .trim()) && !TextUtils.isEmpty(
				m_binding.emailEdit.getText()
				                   .toString()
				                   .trim()) && !TextUtils.isEmpty(m_binding.passwordEdit.getText()
		                                                                                .toString()
		                                                                                .trim()) && !TextUtils.isEmpty(
				m_binding.mobileNumberEdit.getText()
				                          .toString()
				                          .trim()) && !TextUtils.isEmpty(m_binding.dobText.getText()
		                                                                                  .toString()
		                                                                                  .trim()));
	}
	
	/**
	 * Show SnackBar on current view
	 */
	private void m_showSnackBar(String msg, int textColor, boolean login) {
		Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
		snackbar.getView()
		        .setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.editTextBgColor, null));
		((TextView) snackbar.getView()
		                    .findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
		if (login) {
			snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
			snackbar.setActionTextColor(Color.GREEN);
			snackbar.setAction(getResources().getString(R.string.login), v -> finish());
		}
		snackbar.show();
	}
	
	/**
	 * Set default values
	 */
	private void m_setDefault() {
		m_binding.firstNameEdit.getText()
		                       .clear();
		m_binding.surnameEdit.getText()
		                     .clear();
		m_binding.emailEdit.getText()
		                   .clear();
		m_binding.passwordEdit.getText()
		                      .clear();
		m_binding.mobileNumberEdit.getText()
		                          .clear();
		m_binding.dobText.setText("");
		m_binding.firstNameEdit.clearFocus();
		m_binding.surnameEdit.clearFocus();
		m_binding.emailEdit.clearFocus();
		m_binding.passwordEdit.clearFocus();
		m_binding.mobileNumberEdit.clearFocus();
	}
}
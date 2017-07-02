package com.onkarnene.suggestme.views.signup;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.Customer;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ActivityCompat.OnRequestPermissionsResultCallback
{

    private final int REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE = 2;
    @BindView(R.id.firstNameEdit)
    EditText m_firstName;
    @BindView(R.id.surnameEdit)
    EditText m_surname;
    @BindView(R.id.emailEdit)
    EditText m_email;
    @BindView(R.id.passwordEdit)
    EditText m_password;
    @BindView(R.id.mobileNumberEdit)
    EditText m_mobileNumber;
    @BindView(R.id.dobText)
    TextView m_dobText;
    @BindView(R.id.signUpToolbar)
    Toolbar m_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        this.m_toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        this.m_requestDatabasePermission();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        this.m_dobText.setText(date);
    }

    @OnClick(R.id.datePickerImage)
    public void showDatePicker(View view)
    {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setAccentColor(ResourcesCompat.getColor(getResources(), R.color.colorOrange, null));
        dialog.setVersion(DatePickerDialog.Version.VERSION_2);
        dialog.show(getFragmentManager(), "DatePickerDialog");
    }

    /**
     * Check for Database permissions to be granted if OS Version greater than lollipop
     */
    private void m_requestDatabasePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    ActivityCompat.requestPermissions(SignUpActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
                }
                else
                {
                    ActivityCompat.requestPermissions(SignUpActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    break;
                }
                else
                {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * @return false if all fields are empty otherwise true
     */
    private boolean m_validateUserInput()
    {
        return (!TextUtils.isEmpty(this.m_firstName.getText().toString().trim())
                && !TextUtils.isEmpty(this.m_surname.getText().toString().trim())
                && !TextUtils.isEmpty(this.m_email.getText().toString().trim())
                && !TextUtils.isEmpty(this.m_password.getText().toString().trim())
                && !TextUtils.isEmpty(this.m_mobileNumber.getText().toString().trim())
                && !TextUtils.isEmpty(this.m_dobText.getText().toString().trim()));
    }

    @OnClick(R.id.signUpBtn)
    public void signUpClickHandler(View view)
    {
        if (this.m_validateUserInput())
        {
            try
            {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(this.m_dobText.getText().toString());
                String cID = this.m_firstName.getText().toString().toLowerCase() + DateFormat.format("dd", date).toString();
                Customer customer = new Customer();
                customer.setCustomerID(cID.toLowerCase());
                customer.setFirstName(this.m_firstName.getText().toString().toLowerCase());
                customer.setSurname(this.m_surname.getText().toString().toLowerCase());
                customer.setEmail(this.m_email.getText().toString().toLowerCase());
                customer.setPassword(this.m_password.getText().toString().toLowerCase());
                customer.setMobileNumber(this.m_mobileNumber.getText().toString().toLowerCase());
                customer.setDob(this.m_dobText.getText().toString().toLowerCase());
                DatabaseHelper.getInstance(this).saveCustomer(customer);
                this.m_showSnackBar(getResources().getString(R.string.register_success), Color.WHITE, true);
                this.m_setDefault();
                view.setEnabled(false);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                view.setEnabled(true);
                this.m_showSnackBar(getResources().getString(R.string.something_went_wrong), Color.RED, false);
            }
        }
        else
        {
            view.setEnabled(true);
            this.m_showSnackBar(getResources().getString(R.string.all_fields_mandatory), Color.WHITE, false);
        }
    }

    /**
     * Show SnackBar on current view
     */
    private void m_showSnackBar(String msg, int textColor, boolean login)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.editTextBgColor, null));
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
        if (login)
        {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.setAction(getResources().getString(R.string.login), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Set default values
     */
    private void m_setDefault()
    {
        this.m_firstName.getText().clear();
        this.m_surname.getText().clear();
        this.m_email.getText().clear();
        this.m_password.getText().clear();
        this.m_mobileNumber.getText().clear();
        this.m_dobText.setText("");
        this.m_firstName.clearFocus();
        this.m_surname.clearFocus();
        this.m_email.clearFocus();
        this.m_password.clearFocus();
        this.m_mobileNumber.clearFocus();
    }
}
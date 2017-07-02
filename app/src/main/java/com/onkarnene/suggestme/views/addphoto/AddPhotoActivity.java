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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Pair;
import com.onkarnene.suggestme.views.suggestpair.SuggestPairActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPhotoActivity extends AppCompatActivity implements View.OnClickListener
{

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_PICK_IMAGE = 2;
    @BindView(R.id.addPhotoToolbar)
    Toolbar m_toolbar;
    @BindView(R.id.selectShirtImage)
    ImageView m_shirtImage;
    @BindView(R.id.selectPantImage)
    ImageView m_pantImage;
    @BindView(R.id.addPairBtn)
    Button m_addPairBtn;

    private Context m_context;
    private String m_defaultPath;
    private boolean m_isShirt;
    private BottomSheetDialog m_sheetDialog;
    private File m_shirtImagePath;
    private File m_pantImagePath;
    private String m_sourceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        ButterKnife.bind(this);
        this.m_context = this;
        this.m_setupUI();
    }

    @OnClick(R.id.selectShirtBtn)
    public void selectShirtBtnClickHandler(View view)
    {
        this.m_isShirt = true;
        this.m_sheetDialog.show();
    }

    @OnClick(R.id.selectPantBtn)
    public void selectPantBtnClickHandler(View view)
    {
        this.m_isShirt = false;
        this.m_sheetDialog.show();
    }

    /**
     * Initialize UI components
     */
    private void m_setupUI()
    {
        this.m_addPairBtn.setEnabled(false);
        this.m_defaultPath = "android.resource://" + getPackageName() + "/" + R.drawable.place_holder;
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_item, null);
        this.m_sheetDialog = new BottomSheetDialog(this);
        this.m_sheetDialog.setCancelable(true);
        this.m_sheetDialog.setCanceledOnTouchOutside(true);
        this.m_sheetDialog.setContentView(view);
        this.m_shirtImagePath = new File(this.m_defaultPath);
        this.m_pantImagePath = new File(this.m_defaultPath);

        this.m_sourceIntent = getIntent().getStringExtra(AppModel.KEY_SOURCE);
        (view.findViewById(R.id.cameraIcon)).setOnClickListener(this);
        (view.findViewById(R.id.galleryIcon)).setOnClickListener(this);
        this.m_toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null)
        {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            if (this.m_isShirt)
            {
                this.m_shirtImage.setImageBitmap(image);
            }
            else
            {
                this.m_pantImage.setImageBitmap(image);
            }
        }
        else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            if (this.m_isShirt)
            {
                this.m_shirtImage.setImageURI(selectedImage);
            }
            else
            {
                this.m_pantImage.setImageURI(selectedImage);
            }
        }
        this.m_createImageFile();
        this.m_addPairBtn.setEnabled(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cameraIcon:
                this.m_sheetDialog.dismiss();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(cameraIntent, this.REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.galleryIcon:
                this.m_sheetDialog.dismiss();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, this.REQUEST_PICK_IMAGE);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.addPairBtn)
    public void addPair(View view)
    {
        new AddPhotoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Create image file path
     */
    private void m_createImageFile()
    {
        try
        {
            String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String imageFileName = AppModel.customer.getCustomerID() + "_" + currentTime;
            File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + AppModel.customer
                    .getCustomerID() + File.separator);
            if (!storageDir.exists())
            {
                storageDir.mkdirs();
            }
            File file = File.createTempFile(imageFileName, ".jpg", storageDir);
            if (this.m_isShirt)
            {
                this.m_shirtImagePath = file;
            }
            else
            {
                this.m_pantImagePath = file;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Show SnackBar on current view
     */
    private void m_showSnackBar(String msg, int textColor)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.editTextBgColor, null));
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
        snackbar.show();
    }

    /**
     * Save image in local storage
     */
    private void m_saveShirtImage()
    {
        try
        {
            FileOutputStream outStream = new FileOutputStream(this.m_shirtImagePath);
            Bitmap bitmap = ((BitmapDrawable) this.m_shirtImage.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Save image in local storage
     */
    private void m_savePantImage()
    {
        try
        {
            FileOutputStream outStream = new FileOutputStream(this.m_pantImagePath);
            Bitmap bitmap = ((BitmapDrawable) this.m_pantImage.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.doneBtn)
    public void m_doneBtnClickHandler(View view)
    {
        if (this.m_sourceIntent.equalsIgnoreCase(AppModel.FROM_LOGIN))
        {
            startActivity(new Intent(this, SuggestPairActivity.class));
        }
        finish();
    }

    /**
     * Background task to save pair in database
     */
    private class AddPhotoAsyncTask extends AsyncTask<Void, Void, Void>
    {

        private ProgressDialog m_dialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            this.m_dialog = new ProgressDialog(m_context);
            this.m_dialog.setCancelable(true);
            this.m_dialog.setMessage(getResources().getString(R.string.please_wait));
            this.m_dialog.setProgressStyle(R.style.AppCompatAlertDialogStyle);
            this.m_dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            this.m_dialog.dismiss();
            m_addPairBtn.setEnabled(false);
            m_showSnackBar(getResources().getString(R.string.added_success), Color.WHITE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                if (m_shirtImagePath.getAbsolutePath().equalsIgnoreCase(m_defaultPath) || m_pantImagePath.getAbsolutePath().equalsIgnoreCase(m_defaultPath))
                {
                    m_showSnackBar(getResources().getString(R.string.select_pair_confirmation), Color.WHITE);
                }
                else
                {
                    if (m_shirtImagePath != null && m_pantImagePath != null)
                    {
                        m_saveShirtImage();
                        m_savePantImage();
                        Pair pair = new Pair();
                        pair.setCustomerID(AppModel.customer.getCustomerID());
                        pair.setShirtPath(m_shirtImagePath.getAbsolutePath());
                        pair.setPantPath(m_pantImagePath.getAbsolutePath());
                        DatabaseHelper.getInstance(m_context).savePair(pair);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
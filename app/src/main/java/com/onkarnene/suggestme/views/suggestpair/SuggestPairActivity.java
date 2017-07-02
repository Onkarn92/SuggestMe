package com.onkarnene.suggestme.views.suggestpair;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Pair;
import com.onkarnene.suggestme.views.addphoto.AddPhotoActivity;
import com.onkarnene.suggestme.views.history.PairHistoryActivity;
import com.onkarnene.suggestme.views.login.LoginActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuggestPairActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{

    private final int REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE = 2;
    @BindView(R.id.suggestPairToolbar)
    Toolbar m_toolbar;
    @BindView(R.id.suggestedShirtImage)
    ImageView m_suggestedShirt;
    @BindView(R.id.suggestedPantImage)
    ImageView m_suggestedPant;
    private Context m_context;
    private ArrayList<Pair> m_pairs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_pair);
        ButterKnife.bind(this);
        setSupportActionBar(this.m_toolbar);
        this.m_context = this;
        this.m_requestDatabasePermission();
        this.m_pairs = DatabaseHelper.getInstance(this).getPairs(AppModel.GET_SUGGESTED_PAIR);
        SharedPreferences prefs = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE);
        int index = prefs.getInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
        this.m_initView(index);
    }

    /**
     * Initialize view
     */
    private void m_initView(final int index)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (!m_pairs.isEmpty())
                    {
                        if (m_isElementAvailable(index))
                        {
                            File shirtFile = new File(m_pairs.get(index).getShirtPath());
                            File pantFile = new File(m_pairs.get(index).getPantPath());
                            if (shirtFile.exists() && pantFile.exists())
                            {
                                m_suggestedShirt.setImageURI(Uri.parse(shirtFile.getAbsolutePath()));
                                m_suggestedPant.setImageURI(Uri.parse(pantFile.getAbsolutePath()));
                            }
                            else
                            {
                                m_showSnackBar(getResources().getString(R.string.image_missing), ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
                            }
                        }
                        else
                        {
                            SharedPreferences.Editor editor = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE).edit();
                            editor.putInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
                            editor.apply();
                            m_initView(AppModel.DEFAULT_PAIR_INDEX);
                        }
                    }
                    else
                    {
                        m_showSnackBar(getResources().getString(R.string.no_pair_found), Color.WHITE);
                        m_suggestedShirt.setImageResource(R.drawable.place_holder);
                        m_suggestedPant.setImageResource(R.drawable.place_holder);
                    }
                }
                catch (SecurityException | NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.dislikeBtn)
    public void m_dislikeClickHandler(View view)
    {
        SharedPreferences prefs = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE);
        int index = prefs.getInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
        SharedPreferences.Editor editor = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE).edit();
        editor.putInt(AppModel.PREF_KEY_PAIR_INDEX, index + 1);
        editor.apply();
        index += 1;
        this.m_initView(index);
    }

    @OnClick(R.id.savePairBtn)
    public void m_saveClickHandler(View view)
    {
        new SuggestPairAsyncTask().execute();
    }

    /**
     * Check it element at selected index is present or not
     */
    private boolean m_isElementAvailable(int index)
    {
        return index < this.m_pairs.size() && this.m_pairs.contains(this.m_pairs.get(index));
    }

    /**
     * Show SnackBar on current view
     */
    private void m_showSnackBar(String msg, int textColor)
    {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fab), msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.editTextBgColor, null));
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(textColor);
        snackbar.show();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        this.m_pairs = DatabaseHelper.getInstance(this).getPairs(AppModel.GET_SUGGESTED_PAIR);
        SharedPreferences prefs = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE);
        int index = prefs.getInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
        this.m_initView(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_suggest_pair, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_add_photo)
        {
            Intent addPhotoIntent = new Intent(this, AddPhotoActivity.class);
            addPhotoIntent.putExtra(AppModel.KEY_SOURCE, "");
            startActivity(addPhotoIntent);
            return true;
        }
        if (id == R.id.action_logout)
        {
            DatabaseHelper.getInstance(this.m_context).logoutCustomer();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void m_fabClickHandler(View view)
    {
        startActivity(new Intent(this, PairHistoryActivity.class));
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
                    ActivityCompat.requestPermissions(SuggestPairActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_OR_WRITE_EXTERNAL_STORAGE);
                }
                else
                {
                    ActivityCompat.requestPermissions(SuggestPairActivity.this,
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
     * Background task to save pair history in database
     */
    private class SuggestPairAsyncTask extends AsyncTask<Boolean, Void, Boolean>
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
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            this.m_dialog.dismiss();
            if (result)
            {
                m_showSnackBar(getResources().getString(R.string.saved_success), Color.WHITE);
            }
            else
            {
                m_showSnackBar(getResources().getString(R.string.something_went_wrong), Color.WHITE);
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... params)
        {
            try
            {
                SharedPreferences prefs = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE);
                int index = prefs.getInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
                if (!m_pairs.isEmpty() && m_isElementAvailable(index))
                {
                    DatabaseHelper.getInstance(m_context).savePairHistory(m_pairs.get(index));
                    return true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return false;
        }
    }
}
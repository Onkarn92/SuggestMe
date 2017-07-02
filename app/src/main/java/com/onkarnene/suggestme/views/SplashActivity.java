package com.onkarnene.suggestme.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Customer;
import com.onkarnene.suggestme.views.login.LoginActivity;
import com.onkarnene.suggestme.views.suggestpair.SuggestPairActivity;

import java.io.File;

public class SplashActivity extends AppCompatActivity
{

    private Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.m_context = this.getBaseContext();
        new SplashAsyncTask().execute();
    }

    /**
     * @return true if database is available otherwise false.
     */
    private boolean m_checkDataBase()
    {
        try
        {
            File file = getApplicationContext().getDatabasePath(DatabaseHelper.DATABASE_NAME);
            return file.exists();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Background task - Get Last Logged In Customer from DB
     * If customer is null then navigate to LoginActivity
     */
    private class SplashAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private boolean m_isActiveCustomer = false;

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                if (m_checkDataBase())
                {
                    Customer customer = DatabaseHelper.getInstance(m_context).getLastLoggedInCustomer();
                    if (customer != null)
                    {
                        AppModel.customer = customer;
                        this.m_isActiveCustomer = true;
                        SharedPreferences prefs = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE);
                        int index = prefs.getInt(AppModel.PREF_KEY_PAIR_INDEX, AppModel.DEFAULT_PAIR_INDEX);
                        SharedPreferences.Editor editor = getSharedPreferences(AppModel.PREFERENCE_SUGGEST_PAIR, MODE_PRIVATE).edit();
                        editor.putInt(AppModel.PREF_KEY_PAIR_INDEX, index + 1);
                        editor.apply();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (m_isActiveCustomer)
                    {
                        //Navigate to SuggestPairActivity
                        startActivity(new Intent(SplashActivity.this, SuggestPairActivity.class));
                    }
                    else
                    {
                        //Navigate to LoginActivity
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }, 2000);
        }
    }
}
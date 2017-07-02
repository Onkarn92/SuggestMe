package com.onkarnene.suggestme.views.history;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.adapters.PageHistoryAdapter;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Pair;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PairHistoryActivity extends AppCompatActivity implements PageHistoryAdapter.IPairCallback
{

    @BindView(R.id.recyclerView)
    RecyclerView m_recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar m_progressBar;
    @BindView(R.id.historyText)
    TextView m_historyText;
    @BindView(R.id.pairHistoryToolbar)
    Toolbar m_toolbar;

    private Context m_context;
    private PageHistoryAdapter m_pageHistoryAdapter;
    private ArrayList<Pair> m_pairs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_history);
        ButterKnife.bind(this);
        this.m_context = this;
        this.m_toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        this.m_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.m_pageHistoryAdapter = new PageHistoryAdapter(this);
        this.m_pageHistoryAdapter.setPairCallback(this);
        new PairHistoryAsyncTask().execute();
    }

    @Override
    public void onShareButtonClick(Pair pair)
    {
        if (pair != null)
        {
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(Uri.parse(pair.getShirtPath()));
            uris.add(Uri.parse(pair.getPantPath()));

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share)));
        }
    }

    /**
     * Background task to save pair history
     */
    private class PairHistoryAsyncTask extends AsyncTask<Boolean, Void, Boolean>
    {

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result)
            {
                m_progressBar.setVisibility(View.GONE);
                m_recyclerView.setVisibility(View.VISIBLE);
                m_pageHistoryAdapter.setPairs(m_pairs);
                m_recyclerView.setAdapter(m_pageHistoryAdapter);
            }
            else
            {
                m_progressBar.setVisibility(View.GONE);
                m_recyclerView.setVisibility(View.GONE);
                m_historyText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Boolean doInBackground(Boolean... params)
        {
            try
            {
                m_pairs = DatabaseHelper.getInstance(m_context).getPairs(AppModel.GET_PAIR_HISTORY);
                if (!m_pairs.isEmpty())
                {
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
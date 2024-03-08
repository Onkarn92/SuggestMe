package com.onkarnene.suggestme.views.history;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.adapters.PageHistoryAdapter;
import com.onkarnene.suggestme.databinding.ActivityPairHistoryBinding;
import com.onkarnene.suggestme.generics.DatabaseHelper;
import com.onkarnene.suggestme.models.AppModel;
import com.onkarnene.suggestme.models.Pair;

import java.util.ArrayList;

public class PairHistoryActivity extends AppCompatActivity implements PageHistoryAdapter.IPairCallback {
	
	private Context m_context;
	private PageHistoryAdapter m_pageHistoryAdapter;
	private ArrayList<Pair> m_pairs;
	private ActivityPairHistoryBinding m_binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_binding = ActivityPairHistoryBinding.inflate(getLayoutInflater());
		setContentView(m_binding.getRoot());
		this.m_context = this;
		m_binding.pairHistoryToolbar.setNavigationOnClickListener(v -> finish());
		m_binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
		this.m_pageHistoryAdapter = new PageHistoryAdapter(this);
		this.m_pageHistoryAdapter.setPairCallback(this);
		new PairHistoryAsyncTask().execute();
	}
	
	@Override
	public void onShareButtonClick(Pair pair) {
		if (pair != null) {
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
	private class PairHistoryAsyncTask extends AsyncTask<Boolean, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Boolean... params) {
			try {
				m_pairs = DatabaseHelper.getInstance(m_context)
				                        .getPairs(AppModel.GET_PAIR_HISTORY);
				if (!m_pairs.isEmpty()) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				m_binding.progressBar.setVisibility(View.GONE);
				m_binding.recyclerView.setVisibility(View.VISIBLE);
				m_pageHistoryAdapter.setPairs(m_pairs);
				m_binding.recyclerView.setAdapter(m_pageHistoryAdapter);
			} else {
				m_binding.progressBar.setVisibility(View.GONE);
				m_binding.recyclerView.setVisibility(View.GONE);
				m_binding.historyText.setVisibility(View.VISIBLE);
			}
		}
	}
}
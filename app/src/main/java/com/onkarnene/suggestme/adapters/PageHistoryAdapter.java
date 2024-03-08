package com.onkarnene.suggestme.adapters;
/*
 * Created by Onkar Nene on 25-01-2017.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.databinding.ItemPairHistoryBinding;
import com.onkarnene.suggestme.models.Pair;

import java.util.ArrayList;

public class PageHistoryAdapter extends RecyclerView.Adapter<PageHistoryAdapter.ViewHolder> {
	
	private final Context m_context;
	private ArrayList<Pair> m_pairs;
	private IPairCallback m_pairCallback;
	
	/**
	 * Constructor function
	 *
	 * @param context of the activity
	 */
	public PageHistoryAdapter(Context context) {
		this.m_context = context;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(ItemPairHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		try {
			Glide.with(this.m_context)
			     .load(this.m_pairs.get(position)
			                       .getShirtPath())
			     .placeholder(R.drawable.place_holder)
			     .crossFade()
			     .into(holder.m_binding.savedShirtImage);
			Glide.with(this.m_context)
			     .load(this.m_pairs.get(position)
			                       .getPantPath())
			     .placeholder(R.drawable.place_holder)
			     .crossFade()
			     .into(holder.m_binding.savedPantImage);
			holder.setPair(this.m_pairs.get(position));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getItemCount() {
		if (this.m_pairs == null) {
			return 0;
		} else {
			return this.m_pairs.size();
		}
	}
	
	/**
	 * Set list of Pair model
	 *
	 * @param pairs contains data
	 */
	public void setPairs(@NonNull ArrayList<Pair> pairs) {
		this.m_pairs = pairs;
	}
	
	/**
	 * @param pairCallback function to be register
	 */
	public void setPairCallback(IPairCallback pairCallback) {
		this.m_pairCallback = pairCallback;
	}
	
	/**
	 * Holds the view for respective ListItem
	 */
	class ViewHolder extends RecyclerView.ViewHolder {
		
		private Pair m_pair;
		
		ItemPairHistoryBinding m_binding;
		
		/**
		 * Constructor function
		 *
		 * @param binding contains respective ListItem
		 */
		ViewHolder(ItemPairHistoryBinding binding) {
			super(binding.getRoot());
			m_binding = binding;
			m_binding.shareImageIcon.setOnClickListener(this::onShareClickListener);
		}
		
		public void onShareClickListener(View view) {
			if (m_pairCallback != null) {
				m_pairCallback.onShareButtonClick(this.m_pair);
			}
		}
		
		void setPair(Pair pair) {
			this.m_pair = pair;
		}
	}
	
	/**
	 * Callback interface
	 * Listens item click event
	 */
	public interface IPairCallback {
		
		void onShareButtonClick(Pair pair);
	}
}

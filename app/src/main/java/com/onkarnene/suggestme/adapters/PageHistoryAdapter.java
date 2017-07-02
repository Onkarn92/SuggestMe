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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onkarnene.suggestme.R;
import com.onkarnene.suggestme.models.Pair;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PageHistoryAdapter extends RecyclerView.Adapter<PageHistoryAdapter.ViewHolder>
{

    private ArrayList<Pair> m_pairs;
    private Context m_context;
    private IPairCallback m_pairCallback;

    /**
     * Constructor function
     *
     * @param context of the activity
     */
    public PageHistoryAdapter(Context context)
    {
        this.m_context = context;
    }

    /**
     * Set list of Pair model
     *
     * @param pairs contains data
     */
    public void setPairs(@NonNull ArrayList<Pair> pairs)
    {
        this.m_pairs = pairs;
    }

    /**
     * @param pairCallback function to be register
     */
    public void setPairCallback(IPairCallback pairCallback)
    {
        this.m_pairCallback = pairCallback;
    }

    @Override
    public PageHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pair_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        try
        {
            Glide.with(this.m_context)
                    .load(this.m_pairs.get(position).getShirtPath())
                    .placeholder(R.drawable.place_holder)
                    .crossFade()
                    .into(holder.shirtImage);
            Glide.with(this.m_context)
                    .load(this.m_pairs.get(position).getPantPath())
                    .placeholder(R.drawable.place_holder)
                    .crossFade()
                    .into(holder.pantImage);
            holder.setPair(this.m_pairs.get(position));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        if (this.m_pairs == null)
        {
            return 0;
        }
        else
        {
            return this.m_pairs.size();
        }
    }

    /**
     * Callback interface
     * Listens item click event
     */
    public interface IPairCallback
    {
        public void onShareButtonClick(Pair pair);
    }

    /**
     * Holds the view for respective ListItem
     */
    class ViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.savedShirtImage)
        ImageView shirtImage;

        @BindView(R.id.savedPantImage)
        ImageView pantImage;

        private Pair m_pair;

        /**
         * Constructor function
         *
         * @param itemView contains respective ListItem
         */
        ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setPair(Pair pair)
        {
            this.m_pair = pair;
        }

        @OnClick(R.id.shareImageIcon)
        public void onShareClickListener(View view)
        {
            if (m_pairCallback != null)
            {
                m_pairCallback.onShareButtonClick(this.m_pair);
            }
        }
    }
}

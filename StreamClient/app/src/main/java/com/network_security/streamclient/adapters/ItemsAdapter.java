package com.network_security.streamclient.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.network_security.streamclient.R;
import com.network_security.streamclient.model.Item;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 06/05/2017.
 */

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(View view, Item item, int position);
    }

    private ItemClickListener itemClickListener;
    private Context context;
    private ArrayList<Item> items = new ArrayList<>();

    public ItemsAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        setItems(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_stream_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder view = (ItemViewHolder) holder;
        Item item = getItem(position);

        view.image.setImageDrawable(ContextCompat.getDrawable(context, item.getDrawable()));
        view.filename.setText(item.getName());
        view.fileLocation.setText(item.getFile().getAbsolutePath());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItems(ArrayList<Item> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    private Item getItem(int position) {
        return items.get(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;

        @Bind(R.id.imageView)
        ImageView image;
        @Bind(R.id.filename)
        TextView filename;
        @Bind(R.id.file_location)
        TextView fileLocation;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getLayoutPosition();
                itemClickListener.onItemClick(v, getItem(position), position);
            }
        }
    }
}

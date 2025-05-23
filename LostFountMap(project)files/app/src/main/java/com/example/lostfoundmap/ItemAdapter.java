package com.example.lostfoundmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfoundmap.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final List<Item> itemList;

    public ItemAdapter(List<Item> items) {
        this.itemList = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, latlngText;

        public ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.titleText);
            latlngText = view.findViewById(R.id.latlngText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.titleText.setText(item.getTitle());
        holder.latlngText.setText("Lat: " + item.getLatitude() + ", Lng: " + item.getLongitude());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

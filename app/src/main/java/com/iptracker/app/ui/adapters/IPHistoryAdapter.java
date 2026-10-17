package com.iptracker.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.iptracker.app.ui.models.IPHistoryItem;
import com.iptracker.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IPHistoryAdapter extends ArrayAdapter<IPHistoryItem> {
    private final SimpleDateFormat dateFormatter;

    public IPHistoryAdapter(Context context, List<IPHistoryItem> items) {
        super(context, R.layout.ip_history_item, items);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.ip_history_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        IPHistoryItem item = getItem(position);
        if (item != null) {
            bindViewHolder(viewHolder, item);
        }

        return view;
    }

    private void bindViewHolder(ViewHolder holder, IPHistoryItem item) {

        holder.statusIcon.setImageResource(android.R.drawable.presence_online);
        holder.statusIcon.setColorFilter(
                ContextCompat.getColor(
                        getContext(),
                        item.isValid() ? R.color.valid_ip : R.color.invalid_ip
                )
        );


        holder.ipAddressText.setText(item.getIpAddress());
        holder.ipAddressText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.primary_text_light));

        holder.locationText.setText(item.getLocation());
        holder.locationText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));

        holder.timestampText.setText(dateFormatter.format(new Date(item.getTimestamp())));
        holder.timestampText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));

        holder.reliabilityRating.setRating(item.getReliability());
        holder.reliabilityRating.setEnabled(false);
    }

    private static class ViewHolder {
        final ImageView statusIcon;
        final TextView ipAddressText;
        final TextView locationText;
        final TextView timestampText;
        final RatingBar reliabilityRating;

        ViewHolder(View view) {
            statusIcon = view.findViewById(R.id.statusIcon);
            ipAddressText = view.findViewById(R.id.ipAddressText);
            locationText = view.findViewById(R.id.locationText);
            timestampText = view.findViewById(R.id.timestampText);
            reliabilityRating = view.findViewById(R.id.reliabilityRating);
        }
    }
}
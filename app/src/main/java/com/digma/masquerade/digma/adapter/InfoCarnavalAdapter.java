package com.digma.masquerade.digma.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digma.masquerade.digma.R;
import com.digma.masquerade.digma.domain.InfoCarnaval;

import java.util.List;

/**
 * Created by janidham on 12/01/16.
 */
public class InfoCarnavalAdapter extends RecyclerView.Adapter<InfoCarnavalAdapter.InfoCarnavalViewHolder> {

    private List<InfoCarnaval> items;

    public InfoCarnavalAdapter(List<InfoCarnaval> items) {
        this.items = items;
    }

    public static class InfoCarnavalViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDay, txtTitle, txtDescription, txtHour;

        public InfoCarnavalViewHolder(View itemView) {
            super(itemView);
            txtDay = (TextView) itemView.findViewById(R.id.txt_day);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
            txtHour = (TextView) itemView.findViewById(R.id.txt_hour);
        }
    }

    @Override
    public InfoCarnavalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_carnaval_day, parent, false);

        return new InfoCarnavalViewHolder(rootView);
    }

    public String getTitle(int day, int month) {
        String monthText = month == 1 ? "Ene" : "Feb";

        return day + " " + monthText;
    }

    @Override
    public void onBindViewHolder(InfoCarnavalViewHolder holder, int position) {
        final InfoCarnaval carnaval = items.get(position);

        holder.txtTitle.setText(carnaval.getTitle());
        holder.txtDescription.setText(carnaval.getDescription());
        holder.txtHour.setText(carnaval.getHourBegin());

        holder.txtDay.setText(getTitle(carnaval.getDay(), carnaval.getMonth()));

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

}

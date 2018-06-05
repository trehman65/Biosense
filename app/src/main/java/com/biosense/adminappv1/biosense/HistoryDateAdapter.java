package com.biosense.adminappv1.biosense;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HistoryDateAdapter extends RecyclerView.Adapter<HistoryDateAdapter.HistoryDateAdapterViewHolder> {

    private Context ctx;
    private List<HistoryDate> historyDateList;
    private List<String> IDVS;

    public HistoryDateAdapter(Context ctx, List<HistoryDate> historyDateList, List <String> IDVS) {
        this.ctx = ctx;
        this.historyDateList = historyDateList;
        this.IDVS = IDVS;
    }

    @Override
    public HistoryDateAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.layout_reporthistory, null);
        return new HistoryDateAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryDateAdapterViewHolder holder, final int position) {

        HistoryDate dateAndTime = historyDateList.get(position);

        //binding the data with the viewholder views
        holder.textViewDateAndTime.setText(dateAndTime.getDateAndTime());

        if(position % 2==0)
            holder.itemView.setBackgroundColor(Color.WHITE);
        else
            holder.itemView.setBackgroundColor(Color.rgb(220,221,225));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, ResultsActivity.class);
                BioDataHelper bioDataHelper = new BioDataHelper(ctx);

                i.putExtra("PERSON_ID", IDVS.get(position));
                i.putExtra("RECALL_DATE", historyDateList.get(position).getDateAndTime());
                i.putExtra("VOLTAGE_VALUES", "1234512345123451234512345123451234512345");
                i.putExtra("TIME_STAMP", String.valueOf(System.currentTimeMillis()));

                ctx.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return historyDateList.size();
    }

    class HistoryDateAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDateAndTime;
        RelativeLayout relativeLayout;

        public HistoryDateAdapterViewHolder(View itemView) {
            super(itemView);

            textViewDateAndTime = itemView.findViewById(R.id.report_history_date);
            relativeLayout = itemView.findViewById(R.id.recycler_view_row);


        }
    }

}

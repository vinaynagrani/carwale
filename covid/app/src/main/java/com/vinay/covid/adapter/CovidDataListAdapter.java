package com.vinay.covid.adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vinay.covid.R;
import com.vinay.covid.model.CovidEntity;
import java.util.ArrayList;
public class CovidDataListAdapter extends RecyclerView.Adapter<CovidDataListAdapter.CovidHolder> {
    private static final String TAG = "CovidDataListAdapter";
    private ArrayList<CovidEntity> covidEntityArrayList;
    private Context context;
    public CovidDataListAdapter(Context context, ArrayList<CovidEntity> covidEntityArrayList) {
        this.context = context;
        this.covidEntityArrayList = covidEntityArrayList;
    }

    @NonNull
    @Override
    public CovidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CovidDataListAdapter.CovidHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CovidHolder holder, int position) {
        try {
            if (covidEntityArrayList != null) {
                CovidEntity covidEntity = covidEntityArrayList.get(position);
                holder.tv_column1.setText(covidEntity.getCountry());
                holder.tv_column2.setText(String.valueOf(covidEntity.getConfirmed()));
                holder.tv_column3.setText(String.valueOf(covidEntity.getDeaths()));
                holder.tv_column4.setText(String.valueOf(covidEntity.getRecovered()));

                if (position == 0) {
                    holder.tv_column1.setTextColor(context.getResources().getColor(R.color.black));
                    holder.tv_column2.setTextColor(context.getResources().getColor(R.color.black));
                    holder.tv_column3.setTextColor(context.getResources().getColor(R.color.black));
                    holder.tv_column4.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    holder.tv_column1.setTextColor(context.getResources().getColor(R.color.color_text_light_gray));
                    holder.tv_column2.setTextColor(context.getResources().getColor(R.color.color_text_light_gray));
                    holder.tv_column3.setTextColor(context.getResources().getColor(R.color.color_text_light_gray));
                    holder.tv_column4.setTextColor(context.getResources().getColor(R.color.color_text_light_gray));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (covidEntityArrayList != null) {
            count = covidEntityArrayList.size();
        }
        return count;
    }

    public class CovidHolder extends RecyclerView.ViewHolder {
        private TextView tv_column1, tv_column2, tv_column3, tv_column4;

        public CovidHolder(@NonNull View itemView) {
            super(itemView);
            tv_column1 = itemView.findViewById(R.id.tv_column1);
            tv_column2 = itemView.findViewById(R.id.tv_column2);
            tv_column3 = itemView.findViewById(R.id.tv_column3);
            tv_column4 = itemView.findViewById(R.id.tv_column4);


        }
    }


}

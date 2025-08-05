package com.enterprise.pc.applicationlocationfree.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enterprise.pc.applicationlocationfree.R;
import com.enterprise.pc.applicationlocationfree.db.entity.LocationData;

import java.util.List;

/**
 * Created by PC on 2018-05-09.
 */

public class DataPropertiesAdapter extends RecyclerView.Adapter<DataPropertiesAdapter.ViewHolder> {

    private List<LocationData> locationDataList;
    private Context context;

    private static ClickListener clickListener;

    public void setItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ConstraintLayout constraintLayout;

        public ViewHolder(ConstraintLayout constraintLayout) {
            super(constraintLayout);
            constraintLayout.setOnClickListener(this);
            this.constraintLayout = constraintLayout;
        }

        @Override
        public void onClick(View view){

            clickListener.onItemClick(view, getAdapterPosition());

        }

    }

    public interface ClickListener {

        void onItemClick(View view, int position);

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DataPropertiesAdapter(Context context, List<LocationData> locationDataList) {
        this.context = context;
        this.locationDataList = locationDataList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataPropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_properties, parent, false);

        ViewHolder vh = new ViewHolder(constraintLayout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if(locationDataList != null){

            LocationData locationData = this.locationDataList.get(position);

            if(locationData != null){

                if(holder!= null ){

                    if(holder.constraintLayout != null){

                        holder.constraintLayout.findViewById(R.id.buttonDelete).setVisibility(View.GONE);
                        holder.constraintLayout.findViewById(R.id.buttonUpdate).setVisibility(View.GONE);
                        holder.constraintLayout.findViewById(R.id.spaceBottom).setVisibility(View.GONE);

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewTimeValue))
                                .setText(locationData.getFormattedTime()
                                        .replace(context.getString(R.string.str_old_value), context.getString(R.string.str_new_value)));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewLatitudeValue))
                                .setText(Double.toString(locationData.getLatitude()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewLongitudeValue))
                                .setText(Double.toString(locationData.getLongitude()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewAltitudeValue))
                                .setText(Double.toString(locationData.getAltitude()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewAccuracyValue))
                                .setText(Float.toString(locationData.getAccuracy()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewSpeedValue))
                                .setText(Float.toString(locationData.getSpeed()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewBearingValue))
                                .setText(Float.toString(locationData.getBearing()));

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewProviderValue))
                                .setText(locationData.getProvider());

                        setVisibilityOfInformationView(holder.constraintLayout);

                        ((TextView)holder.constraintLayout.findViewById(R.id.textViewInformationValue))
                                .setText(locationData.getInformation());

                    }
                }

            }
        }

    }


    private void setVisibilityOfInformationView(ConstraintLayout constraintLayout) {

        if(constraintLayout != null) {
            ((TextView) constraintLayout.findViewById(R.id.textViewInformationLabel)).setVisibility(View.VISIBLE);
            ((TextView) constraintLayout.findViewById(R.id.textViewInformationLabelPunctuation)).setVisibility(View.VISIBLE);
            ((TextView) constraintLayout.findViewById(R.id.textViewInformationValue)).setVisibility(View.VISIBLE);
        }
    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        int itemCount = -1;
        if(this.locationDataList != null){

            itemCount = this.locationDataList.size();
        }

        return itemCount;
    }

}

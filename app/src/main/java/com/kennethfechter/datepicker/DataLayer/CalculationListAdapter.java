package com.kennethfechter.datepicker.DataLayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kennethfechter.datepicker.R;
import com.kennethfechter.datepicker.Services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kfechter on 6/1/2016.
 */

public class CalculationListAdapter extends RecyclerView.Adapter<CalculationListAdapter.ItemViewHolder> {

    private List<Calculation> calculatedIntervals;
    private Context mContext;
    private  ItemChangedInterface itemChangeListener;
    private SparseBooleanArray selectedCalculations;

    private boolean performAnimations;
    private boolean actionMode;

    public boolean PerformAnimations(){
        return performAnimations;
    }

    public boolean IsActionMode() {
        return actionMode;
    }

    public void SetActionMode(boolean actionMode){
        this.actionMode = actionMode;
    }

    public interface ItemChangedInterface {
        void ItemChanged();
    }

    public ItemChangedInterface ItemChangeListener;

    public CalculationListAdapter(List<Calculation> calculatedIntervals, Context appContext, ItemChangedInterface itemChangeListener, boolean calculationsArchived, boolean actionMode) {
        this.calculatedIntervals = calculatedIntervals;
        this.mContext = appContext;
        this.itemChangeListener = itemChangeListener;
        this.selectedCalculations = new SparseBooleanArray();
        this.performAnimations = calculationsArchived;
        this.actionMode = actionMode;
        this.itemChangeListener = itemChangeListener;
    }

    @Override
    public int getItemCount(){
        return calculatedIntervals.size();
    }

    @Override
    public void onBindViewHolder(ItemViewHolder calculationViewHolder, int position){
            Calculation calculatedInterval = calculatedIntervals.get(position);
            Options intervalOptions = calculatedInterval.GetOptions();
            calculationViewHolder.exclusionsText.setText(intervalOptions.GetExclusionText());
             String intervalText = String.format("The Calculated interval is: %s Days ", calculatedInterval.GetCalculatedInterval());
            calculationViewHolder.itemId.setText(calculatedInterval.getId().toString());
            calculationViewHolder.intervalText.setText(intervalText);
            calculationViewHolder.itemTitleText.setText(calculatedInterval.GetStartDate() + " - " + calculatedInterval.GetEndDate());
           ((CardView)calculationViewHolder.itemView.findViewById(R.id.card_view)).setBackgroundColor(selectedCalculations.valueAt(position) ? Color.LTGRAY : Color.WHITE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        final View calculationView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.calculation_card, viewGroup, false);
        return new ItemViewHolder(calculationView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView itemId;
        protected TextView intervalText;
        protected TextView exclusionsText;
        protected TextView itemTitleText;

        public ItemViewHolder(View v){
            super(v);
            itemId = (TextView) v.findViewById(R.id.txtHiddenID);
            intervalText = (TextView) v.findViewById(R.id.txtCalculatedInterval);
            exclusionsText = (TextView) v.findViewById(R.id.txtExclusions);
            itemTitleText = (TextView) v.findViewById(R.id.txtCalculationTitle);
        }

    }

    // Methods for actionMode

    public void ToggleSelections(int pos){
        if (selectedCalculations.get(pos, false)){
            selectedCalculations.delete(pos);
        } else {
            selectedCalculations.put(pos,true);
        }
        notifyItemChanged(pos);
    }

    public void ClearSelections(){
        selectedCalculations.clear();
        notifyDataSetChanged();
        itemChangeListener.ItemChanged();
    }

    public int getSelectedItemCount(){
        return selectedCalculations.size();
    }

    public List<Integer> GetSelectedItems(){
        List<Integer> items = new ArrayList<Integer>(selectedCalculations.size());
        for (int i = 0; i < selectedCalculations.size(); i++){
            items.add(selectedCalculations.keyAt(i));
        }
        return items;
    }

    public List<Long> getItemIdsFromPosition(){
        List<Long> itemIds = new ArrayList<Long>(selectedCalculations.size());
        for (int i = 0; i < selectedCalculations.size(); i++){
            itemIds.add(calculatedIntervals.get(selectedCalculations.indexOfKey(i)).getId());
        }
        return itemIds;
    }

    public void AddInternalItem(long itemId, int position){
        this.calculatedIntervals.add(position, DatabaseService.getCalculation(itemId));
        notifyDataSetChanged();
    }

    public Calculation getCalculationAtPosition(int position) {
        return calculatedIntervals.get(position);
    }

}

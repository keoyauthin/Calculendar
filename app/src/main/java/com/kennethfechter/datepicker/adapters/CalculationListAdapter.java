package com.kennethfechter.datepicker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kennethfechter.datepicker.R;
import com.kennethfechter.datepicker.entities.Calculation;
import com.kennethfechter.datepicker.entities.Options;
import com.kennethfechter.datepicker.utilities.DatabaseUtilities;

import java.util.ArrayList;
import java.util.List;

public class CalculationListAdapter extends RecyclerView.Adapter<CalculationListAdapter.ItemViewHolder>{

    private final List<Calculation> calculatedIntervals;
    private  ItemChangedInterface itemChangeListener;
    private final SparseBooleanArray selectedCalculations;

    private final boolean performAnimations;
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
        @SuppressWarnings("EmptyMethod")
        void ItemChanged();
    }

    public ItemChangedInterface ItemChangeListener;

    public CalculationListAdapter(List<Calculation> calculatedIntervals, Context appContext, ItemChangedInterface itemChangeListener) {
        this.calculatedIntervals = calculatedIntervals;
        this.itemChangeListener = itemChangeListener;
        this.selectedCalculations = new SparseBooleanArray();
        this.performAnimations = false;
        this.actionMode = false;
        this.itemChangeListener = itemChangeListener;
    }

    @Override
    public int getItemCount(){
        return calculatedIntervals.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ItemViewHolder calculationViewHolder, int position){
        Calculation calculatedInterval = calculatedIntervals.get(position);
        Options intervalOptions = calculatedInterval.GetOptions();
        calculationViewHolder.exclusionsText.setText(intervalOptions.GetExclusionText());
        String intervalText = String.format("The Calculated interval is: %s Days ", calculatedInterval.GetCalculatedInterval());
        calculationViewHolder.itemId.setText(calculatedInterval.getId().toString());
        calculationViewHolder.intervalText.setText(intervalText);
        calculationViewHolder.itemTitleText.setText(calculatedInterval.GetStartDate() + " - " + calculatedInterval.GetEndDate());
        calculationViewHolder.itemView.findViewById(R.id.card_view).setBackgroundColor(selectedCalculations.valueAt(position) ? Color.LTGRAY : Color.WHITE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        final View calculationView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.calculation_card, viewGroup, false);
        return new ItemViewHolder(calculationView);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView itemId;
        final TextView intervalText;
        final TextView exclusionsText;
        final TextView itemTitleText;

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
        List<Integer> items = new ArrayList<>(selectedCalculations.size());
        for (int i = 0; i < selectedCalculations.size(); i++){
            items.add(selectedCalculations.keyAt(i));
        }
        return items;
    }

    public List<Long> getItemIdsFromPosition(){
        List<Long> itemIds = new ArrayList<>(selectedCalculations.size());
        for (int i = 0; i < selectedCalculations.size(); i++){
            itemIds.add(calculatedIntervals.get(selectedCalculations.indexOfKey(i)).getId());
        }
        return itemIds;
    }

    public void AddInternalItem(long itemId, int position){
        this.calculatedIntervals.add(position, DatabaseUtilities.getCalculation(itemId));
        notifyDataSetChanged();
    }

    public Calculation getCalculationAtPosition(int position) {
        return calculatedIntervals.get(position);
    }
}

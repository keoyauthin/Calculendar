package com.kennethfechter.datepicker.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kennethfechter.datepicker.R;
import com.kennethfechter.datepicker.adapters.CalculationListAdapter;
import com.kennethfechter.datepicker.utilities.DatabaseUtilities;

public class CalculationsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "fragmentIndex";

    private OnFragmentInteractionListener mListener;

    private RecyclerView calculationsList;

    private boolean viewArchives;

    public CalculationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentIndex The index o 1.
     * @return A new instance of fragment CalculationsFragment.
     */
    public static CalculationsFragment newInstance(int fragmentIndex) {
        CalculationsFragment fragment = new CalculationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, fragmentIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int fragmentIndex = getArguments().getInt(ARG_PARAM1);

            switch (fragmentIndex)
            {
                case 1:
                    viewArchives = false;
                    break;
                case 2:
                    viewArchives = true;
                    break;
                default:
                    viewArchives = false;
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View calculationsView = inflater.inflate(R.layout.fragment_calculations, container, false);
        calculationsList = (RecyclerView) calculationsView.findViewById(R.id.calculationsList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        calculationsList.setLayoutManager(llm);
        UpdateListView();
        return calculationsView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        @SuppressWarnings("EmptyMethod")
        void onFragmentInteraction(Uri uri);
    }

    private void UpdateListView(){
        calculationsList.setAdapter(new CalculationListAdapter(DatabaseUtilities.getCalculations(viewArchives), getContext(), new CalculationListAdapter.ItemChangedInterface() {
            @Override
            public void ItemChanged() {

            }
        }));
    }
}

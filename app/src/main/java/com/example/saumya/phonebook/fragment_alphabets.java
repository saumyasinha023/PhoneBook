package com.example.saumya.phonebook;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Created by saumya on 11/3/15.
 */
public class fragment_alphabets extends Fragment {
     OnSearchItemListener mCallback;
    private ArrayList<String> list;
    GridView grid;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_alphabets,container,false);
        list=new ArrayList<String>();
        grid=(GridView) view.findViewById(R.id.gridView1);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
      
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public String mSearchTerm;
            public boolean mSearchQueryChanged;

            @Override
            public boolean onQueryTextSubmit(String queryText) {
                // Nothing needs to happen when the user submits the search string
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Called when the action bar search text has changed.  Updates
                // the search filter, and restarts the loader to do a new query
                // using the new search string.
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

                // Don't do anything if the filter is empty
                if (mSearchTerm == null && newFilter == null) {
                    return true;
                }

                // Don't do anything if the new filter is the same as the current filter
                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                    return true;
                }

                // Updates current filter to new filter
                mSearchTerm = newFilter;
                Log.e("log", "" + mSearchTerm);


                // Restarts the loader. This triggers onCreateLoader(), which builds the
                // necessary content Uri from mSearchTerm.
                mSearchQueryChanged = true;
mCallback.onItemFound(mSearchTerm,searchView);
                return true;
            }
        });

        String n="";
        for(char c='A';c<='Z';c++){
            n=""+c;

            list.add(n);
            if(c=='Z'){
                list.add(" ");
                list.add(" ");
            }
        }
        ArrayAdapter<String> adp=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,list);
        grid.setAdapter(adp);

        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnSearchItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchItemListener");
        }
    }


}

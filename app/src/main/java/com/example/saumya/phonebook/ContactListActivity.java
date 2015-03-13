package com.example.saumya.phonebook;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;

/**
 * Created by saumya on 11/3/15.
 */

public class ContactListActivity extends FragmentActivity implements
        ContactListFragment.OnContactsInteractionListener, OnSearchItemListener {

    private static final String TAG = "ContactsListActivity";

    private boolean isTwoPaneLayout;
    ContactListFragment conFrag = new ContactListFragment();

    private boolean isSearchResultView = false;
    private String mSearchTerm;
    ContactListFragment mContactsListFragment;
    private boolean mSearchQueryChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("TAG","1");
        if (BuildConfig.DEBUG) {
            Log.e("TAG","2");
            Utils.enableStrictMode();
        }
        Log.e("TAG","3");
        super.onCreate(savedInstanceState);
        Log.e("TAG","4");
        setContentView(R.layout.activity_main);
        Log.e("TAG","5");


        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            Log.e("TAG","6");
            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            Log.e("TAG","7");
            mContactsListFragment = (ContactListFragment)
                    getSupportFragmentManager().findFragmentById(R.layout.fragment_listview);
            Log.e("TAG","8");
            isSearchResultView = true;
            Log.e("TAG","9");
            mContactsListFragment.setSearchQuery(searchQuery);
            Log.e("TAG","10");
            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
            Log.e("TAG","11");
            setTitle(title);
            Log.e("TAG","12");
        }
        Log.e("TAG","13");
    }

    @Override
    public void onContactSelected(Uri contactUri) {

    }

    @Override
    public void onSelectionCleared() {

    }

    @Override
    public boolean onSearchRequested() {

        return !isSearchResultView && super.onSearchRequested();
    }

    @Override
    public void onItemFound(String param, SearchView searchView) {
        Log.e("app", param);
    mContactsListFragment=ContactListFragment.getInstance();
        Log.e("instance",""+mContactsListFragment);
        mContactsListFragment.search(param,searchView);

        boolean check = true;
        //new ContactListFragment(param, searchView, check);
    }
}

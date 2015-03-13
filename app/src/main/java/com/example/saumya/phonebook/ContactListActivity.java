package com.example.saumya.phonebook;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {

            String searchQuery = getIntent().getStringExtra(SearchManager.QUERY);
            ContactListFragment mContactsListFragment = (ContactListFragment)
                    getSupportFragmentManager().findFragmentById(R.layout.fragment_listview);

            isSearchResultView = true;
            mContactsListFragment.setSearchQuery(searchQuery);

            String title = getString(R.string.contacts_list_search_results_title, searchQuery);
            setTitle(title);
        }
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

        boolean check = true;
        new ContactListFragment(param, searchView, check);
    }
}

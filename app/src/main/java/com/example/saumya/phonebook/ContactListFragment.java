

package com.example.saumya.phonebook;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.QuickContactBadge;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class ContactListFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ContactsListFragment";

    private static final String STATE_PREVIOUSLY_SELECTED_KEY =
            "com.example.saumya.myphonebook.SELECTED_ITEM";

    private ContactsAdapter mAdapter;
    private ImageLoader mImageLoader;
    private String mSearchTerm;

    private OnContactsInteractionListener mOnContactSelectedListener;

    private int mPreviouslySelectedSearchItem = 0;

    private boolean mSearchQueryChanged;
    private static ContactListFragment contct;

    private boolean mIsSearchResultView = false;

    private boolean check;

    public ContactListFragment() {

    }
//    public ContactListFragment(String param, SearchView searchView, boolean check) {
//        this.mSearchTerm = param;
//        this.searchView = searchView;
//        this.check = check;
//        Log.e("log", "called");
//    }
    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mIsSearchResultView = false;
        } else {
            mSearchTerm = query;
            mIsSearchResultView = true;
        }
    }

    public static ContactListFragment getInstance(){
        return contct;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
               mAdapter = new ContactsAdapter(getActivity());
        Log.e("log", "inoncreate");
        if (savedInstanceState != null) {

            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
            mPreviouslySelectedSearchItem =
                    savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }
        mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {

                return loadContactPhotoThumbnail((String) data, getImageSize());
            }
        };
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

        mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);
        if (check == true) {
            Log.e("log", "in if");

            if (Utils.hasHoneycomb()) {
                Log.e("log", "one");

                  }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("log", "inoncreateview");
        return inflater.inflate(R.layout.fragment_listview, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("log", "inactivitycreate");
            contct=this;
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageLoader.setPauseWork(true);
                } else {
                    mImageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });


        if (mPreviouslySelectedSearchItem == 0) {

            getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.e("TAG","Attached");

        try {
            mOnContactSelectedListener = (OnContactsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mImageLoader.setPauseWork(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        final Cursor cursor = mAdapter.getCursor();

        cursor.moveToPosition(position);

        final Uri uri = Contacts.getLookupUri(
                cursor.getLong(ContactsQuery.ID),
                cursor.getString(ContactsQuery.LOOKUP_KEY));

        mOnContactSelectedListener.onContactSelected(uri);


    }

    private void onSelectionCleared() {

        mOnContactSelectedListener.onSelectionCleared();

        getListView().clearChoices();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.contact_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchTerm)) {

            outState.putString(SearchManager.QUERY, mSearchTerm);

            Log.e("log", "" + mSearchTerm);
            Log.e("log", "" + SearchManager.QUERY);

            outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, getListView().getCheckedItemPosition());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                final Intent intent = new Intent(Intent.ACTION_INSERT, Contacts.CONTENT_URI);
                startActivity(intent);
                break;

            case R.id.menu_search:
                if (!Utils.hasHoneycomb()) {
                    getActivity().onSearchRequested();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            if (mSearchTerm == null) {

                contentUri = ContactsQuery.CONTENT_URI;
            } else {

                contentUri =
                        Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
            }
            return new CursorLoader(getActivity(),
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mAdapter.swapCursor(data);

            if (!TextUtils.isEmpty(mSearchTerm) && mSearchQueryChanged) {

                if (data != null && data.moveToPosition(mPreviouslySelectedSearchItem)) {

                    final Uri uri = Uri.withAppendedPath(
                            Contacts.CONTENT_URI, String.valueOf(data.getLong(ContactsQuery.ID)));
                    mOnContactSelectedListener.onContactSelected(uri);
                    getListView().setItemChecked(mPreviouslySelectedSearchItem, true);
                } else {

                    onSelectionCleared();
                }
                mPreviouslySelectedSearchItem = 0;
                mSearchQueryChanged = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {

            mAdapter.swapCursor(null);
        }
    }
    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        getActivity().getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);

        final DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return (int) typedValue.getDimension(metrics);
    }

    private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

        if (!isAdded() || getActivity() == null) {
            return null;
        }
        AssetFileDescriptor afd = null;

        try {
            Uri thumbUri;
            if (Utils.hasHoneycomb()) {
                thumbUri = Uri.parse(photoData);
            } else {
                final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);

                thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
            }
            afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

            FileDescriptor fileDescriptor = afd.getFileDescriptor();

            if (fileDescriptor != null) {

                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        fileDescriptor, imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Contact photo thumbnail not found for contact " + photoData
                        + ": " + e.toString());
            }
        } finally {

            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {

                }
            }
        }

        return null;
    }

    private class ContactsAdapter extends CursorAdapter implements SectionIndexer {
        private LayoutInflater mInflater;
        private AlphabetIndexer mAlphabetIndexer;
        private TextAppearanceSpan highlightTextSpan;

        public ContactsAdapter(Context context) {
            super(context, null, 0);

            mInflater = LayoutInflater.from(context);

            final String alphabet = context.getString(R.string.alphabet);

            mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

            highlightTextSpan = new TextAppearanceSpan(getActivity(), R.color.highlighted_text_material_dark);
        }

        private int indexOfSearchQuery(String displayName) {
            if (!TextUtils.isEmpty(mSearchTerm)) {
                Log.e("display name",""+displayName);
                return displayName.toLowerCase(Locale.getDefault()).indexOf(
                        mSearchTerm.toLowerCase(Locale.getDefault()));

            }
            return -1;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

            final View itemLayout =
                    mInflater.inflate(R.layout.fragment_list_item, viewGroup, false);

            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
            holder.icon = (QuickContactBadge) itemLayout.findViewById(android.R.id.icon);

            itemLayout.setTag(holder);
            return itemLayout;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder holder = (ViewHolder) view.getTag();

            final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

            final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            final int startIndex = indexOfSearchQuery(displayName);

            if (startIndex == -1) {

                holder.text1.setText(displayName);

                if (TextUtils.isEmpty(mSearchTerm)) {

                    holder.text2.setVisibility(View.GONE);
                } else {

                    holder.text2.setVisibility(View.VISIBLE);
                }
            } else {
                final SpannableString highlightedName = new SpannableString(displayName);

                highlightedName.setSpan(highlightTextSpan, startIndex,
                        startIndex + mSearchTerm.length(), 0);
                holder.text2.setVisibility(View.GONE);
            }
            final Uri contactUri = Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));

            holder.icon.assignContactUri(contactUri);

            mImageLoader.loadImage(photoUri, holder.icon);
        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {

            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }

        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }

        @Override
        public Object[] getSections() {
            return mAlphabetIndexer.getSections();
        }

        @Override
        public int getPositionForSection(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getPositionForSection(i);
        }

        @Override
        public int getSectionForPosition(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getSectionForPosition(i);
        }

        private class ViewHolder {
            TextView text1;
            TextView text2;
            QuickContactBadge icon;
        }
    }

    public interface OnContactsInteractionListener {

        public void onContactSelected(Uri contactUri);

        public void onSelectionCleared();
    }

    public interface ContactsQuery {

        final static int QUERY_ID = 1;

        final static Uri CONTENT_URI = Contacts.CONTENT_URI;

        final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

        @SuppressLint("InlinedApi")
        final static String SELECTION =
                (Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) +
                        "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                Utils.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY : Contacts.DISPLAY_NAME;

        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                Contacts._ID,

                Contacts.LOOKUP_KEY,

                Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,

                Utils.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI : Contacts._ID,

                SORT_ORDER,
        };
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }

    public void search(String param, SearchView searchView){
   this.mSearchTerm=param;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {

                Log.e("log", "2");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ;
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                Log.e("log", "3");

                if (mSearchTerm == null && newFilter == null) {
                    return true;
                }
                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                    Log.e("log", "4");
                    return true;
                }
                mSearchTerm = newFilter;
                Log.e("log", "" + mSearchTerm);

                Log.e("log", "5");
                mSearchQueryChanged = true;
                getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactListFragment.this);

                Log.e("log", "6");
                return true;
            }
        });


        if (mSearchTerm != null) {

            final String savedSearchTerm = mSearchTerm;
            Log.e("log", "10");

            if (Utils.hasICS()) {

            }
            Log.e("log", "" + mSearchTerm);
            Log.e("log", "" + SearchManager.QUERY);

            searchView.setQuery(savedSearchTerm, false);
        }
    }
}

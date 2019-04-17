package uk.thinkling.rnd.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


public class SwipeView extends AppCompatActivity implements ShakeEventManager.OnShakeListener {

    private static final int EDIT_REQUEST_CODE = 100;

    public static ArrayList<ArrayList<String>> itemArrayListArray = new ArrayList<>();
    public static ArrayList<String> titleArrayList;

    private ShakeEventManager shakeMan;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    ListPagerAdapter mListPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Load lists from file or set defaults for some reason, | is not good delimiter
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String storedTitles = preferences.getString("titles", null);
            String storedLists = preferences.getString("lists", null);

            if (storedTitles != null) {
                titleArrayList = new ArrayList(Arrays.asList(storedTitles.split(",")));
                Log.d("LOADING TITLES", storedTitles);
                String[] al = storedLists.split(":");
                itemArrayListArray = new ArrayList();
                for (String anAl : al) {
                    Log.d("LOADING LIST", " adding " + anAl);
                    if (anAl.length() > 2)  // smallest valid entry would be "a,b"
                        itemArrayListArray.add(new ArrayList(Arrays.asList(anAl.split(","))));
                }
                // TODO check the lists match in size, try catch and set defaults etc?
                if (titleArrayList.isEmpty() || (titleArrayList.size() != itemArrayListArray.size())) throw new Exception("Titles and Lists out of sync, restoring defaults");
                // TODO check the delimiters not used in text.

            } else {
                throw new Exception("No Preferences found - setting up default lists");
            }
        } catch (Exception e){
            Log.d("LOADING LIST",  e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            LoadDefaultLists();
        }


        setContentView(R.layout.activity_swipe_view);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mListPagerAdapter = new ListPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mListPagerAdapter);

        shakeMan = new ShakeEventManager(this);
        shakeMan.init(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        String listString="";
        //Save lists to file
        // TODO check the delimiters not used in text.
        // TODO move delimiters to strings file

        for (ArrayList<String> a : itemArrayListArray)
        {
            listString += TextUtils.join(",", a) + ":";
        }
        Log.d("SAVING", listString);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("titles", TextUtils.join(",", titleArrayList));
        editor.putString("lists", listString);
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",  Toast.LENGTH_SHORT).show();

        //TODO Check device supported Accelerometer sensor or not
        //if (ShakeEventManager.isSupported(this)) {

        //Start Accelerometer Listening
        shakeMan.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeMan.deregister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_swipe_view, menu);
        return true;
    }

    @Override
    public void onShake() {
        // run the same as only_one
        if (mListPagerAdapter.getCurrentFragment().listArrayAdapter.getCount()<2) mListPagerAdapter.getCurrentFragment().resetList();
        while (mListPagerAdapter.getCurrentFragment().removeOne());
    }

    public void LoadDefaultLists() {
        titleArrayList = new ArrayList<>(Arrays.asList("The Magic 8-ball says", "The roll of the die is", "The Coin shows","The answer is", "A smart Investor would","I'm drinking"));
        itemArrayListArray = new ArrayList<>();
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("It is certain.","It is decidedly so.","Without a doubt.","Yes - definitely.","You may rely on it","As I see it - yes.","Most likely.","Outlook good.","Yes.","Signs point to yes.","Reply hazy - try again.","Ask again later.","Better not tell you now.","Cannot predict now.","Concentrate and ask again.","Don't count on it.","My reply is no.","My sources say no.","Outlook not so good.","Very doubtful.")));
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX")));
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("HEADS", "TAILS")));
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("YES","NO","MAYBE")));
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("BUY","HOLD","SELL")));
        itemArrayListArray.add(new ArrayList<>( Arrays.asList("tea","wine","coffee","lager","ale","vodka","gin","bubbles")));
    }
    public void doEditActivity(int listID, String listTitle, ArrayList<String> listStrings) {
        Intent intent = new Intent(this, EditListActivity.class);
        intent.putExtra(getString(R.string.EXTRA_LIST_ID), listID);
        intent.putExtra(getString(R.string.EXTRA_LIST_TITLE), listTitle);
        intent.putStringArrayListExtra(getString(R.string.EXTRA_LIST_ITEMS), listStrings);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch ( item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_delete: //NB: once we have ID, we could use Main data elements rather than fragment data
                if (titleArrayList.size() > 1) {
                    titleArrayList.remove(mListPagerAdapter.getCurrentFragment().listID - 1);
                    itemArrayListArray.remove(mListPagerAdapter.getCurrentFragment().listID - 1);
                    mListPagerAdapter.notifyDataSetChanged();
                } else {
                    // TODO - dialog for this, or disable the option when deleting down to one
                }
                return true;

            case R.id.action_new: //NB: once we have ID, we could use Main data elements rather than fragment data
                titleArrayList.add("");
                itemArrayListArray.add(new ArrayList<String>());
                doEditActivity(titleArrayList.size(), "New List Title", new ArrayList<>( Arrays.asList("Option1","Option2","Option3")));
            return true;

            case R.id.action_edit: //NB: once we have ID, we could use Main data elements rather than fragment data
                doEditActivity(mListPagerAdapter.getCurrentFragment().listID, mListPagerAdapter.getCurrentFragment().listTitle, mListPagerAdapter.getCurrentFragment().listItemsArrayList);
                return true;

            case R.id.action_reset:
                LoadDefaultLists();
                mListPagerAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_all:
                mListPagerAdapter.getCurrentFragment().resetList();
                return true;

            case R.id.action_remove_one:
                mListPagerAdapter.getCurrentFragment().removeOne();
                return true;

            case R.id.action_only_one: //could just be a click on the screen or a shake
                if (mListPagerAdapter.getCurrentFragment().listArrayAdapter.getCount()<2) mListPagerAdapter.getCurrentFragment().resetList();
                while (mListPagerAdapter.getCurrentFragment().removeOne());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Check which request we're responding to
        if (requestCode == EDIT_REQUEST_CODE) {
            // Make sure the request was successful - if not, e.g. cancelled then we ignore
            if (resultCode == RESULT_OK) {

                //load the intent, extract values, store and repopulate list by notify change
                final int listID = intent.getIntExtra(getString(R.string.EXTRA_LIST_ID),0);
                final String listTitle = intent.getStringExtra(getString(R.string.EXTRA_LIST_TITLE));
                final ArrayList<String> listItems = intent.getStringArrayListExtra(getString(R.string.EXTRA_LIST_ITEMS));
                titleArrayList.set(listID - 1, listTitle);
                itemArrayListArray.set(listID - 1, listItems);
                // notifydatasetchanged will do full refresh of all lists
                mListPagerAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(listID-1);

            }
        }
    }
/********************************************************************************/
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ListPagerAdapter extends FragmentPagerAdapter {

        ListFragment mCurrentFragment;

        public ListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public ListFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ListFragment (defined as a static inner class below).
            return ListFragment.newInstance(position + 1, titleArrayList.get(position), itemArrayListArray.get(position));
        }

        @Override
        public int getCount() {
            // Show total pages based on array length
            return titleArrayList.size();
        }

        @Override
        /* HACK - forces update of all pages on notify and not just reuse bundle data */
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return titleArrayList.get(position).toUpperCase(l);
        }


        // when primary Item (ie. visible item is swiped to, store the Fragment as mCurrentFragment
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = (ListFragment) object; //Keep reference to object
            }

            super.setPrimaryItem(container, position, object);
        }

        public ListFragment getCurrentFragment(){
            return mCurrentFragment;
        }

    }

    /**
     * A placeholder fragment containing a simple view based on title text and listview
     */
    public static class ListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ArrayAdapter<String> listArrayAdapter;
        ArrayList<String> listItemsArrayList;
        int listID;
        String listTitle;
        ListView mListView;

        /**
         * Returns a new instance of this fragment for the given section
         * number and passes in bundled args
         */
        public static ListFragment newInstance(int sectionNumber, String sectionTitle, ArrayList<String> sectionStrings) {
            ListFragment fragment = new ListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ListFragment() {
        }

        public Boolean removeOne() {
            if (listArrayAdapter.getCount() > 1) {
                listArrayAdapter.remove(listArrayAdapter.getItem((int) (Math.random() * listArrayAdapter.getCount()))); // remove one element at random
                listArrayAdapter.notifyDataSetChanged();
                return true;
            }
            return false;
        }

        public void resetList() {
            listArrayAdapter.clear();
            for (String aListItemsArrayList : listItemsArrayList) {
                listArrayAdapter.add(aListItemsArrayList);
            }
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_swipe_view, container, false);
            Bundle args = getArguments();
            listID = args.getInt(ARG_SECTION_NUMBER);
            listTitle=titleArrayList.get(listID-1);
            listItemsArrayList=itemArrayListArray.get(listID-1);

            mListView = (ListView)rootView.findViewById(R.id.listView);

            listArrayAdapter = new ArrayAdapter<>(rootView.getContext(), R.layout.centred_list_layout, new ArrayList<>(listItemsArrayList));
            mListView.setAdapter(listArrayAdapter);
            return rootView;
        }
    }
}

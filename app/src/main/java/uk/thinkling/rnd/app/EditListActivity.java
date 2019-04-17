package uk.thinkling.rnd.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class EditListActivity extends AppCompatActivity {

    ArrayList<String> listItemsArrayList;
    ArrayAdapter<String> listArrayAdapter;
    int listID=0;
    ListView myListView;
    EditText titleEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Intent intent = getIntent();
        // Receiving the Data
        listID = intent.getIntExtra(getString(R.string.EXTRA_LIST_ID),0);
        String listTitle = intent.getStringExtra(getString(R.string.EXTRA_LIST_TITLE));
        listItemsArrayList = intent.getStringArrayListExtra(getString(R.string.EXTRA_LIST_ITEMS));

        // add some blanks onto the list to allow editing - this is not beautiful, but is efficient code
        listItemsArrayList.addAll(Arrays.asList("","","","","",""));

        titleEditText = (EditText) findViewById(R.id.text2);
        titleEditText.setText(listTitle);

        listArrayAdapter = new CustomArrayListAdapter(this, R.layout.centred_list_layout_edit, listItemsArrayList);
        myListView = (ListView) findViewById(R.id.listView1);
        myListView.setAdapter(listArrayAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch ( item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_save: //

                myListView.requestFocus(); // to trigger onFocusChange for any edited views
                Intent intent = new Intent(this, EditListActivity.class);
                intent.putExtra(getString(R.string.EXTRA_LIST_ID), listID);
                intent.putExtra(getString(R.string.EXTRA_LIST_TITLE), titleEditText.getText().toString());
                listItemsArrayList.removeAll(Arrays.asList("", null)); // remove all blank entries
                intent.putStringArrayListExtra(getString(R.string.EXTRA_LIST_ITEMS), listItemsArrayList);
                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.action_cancel:
                setResult(RESULT_CANCELED);
                finish();
                return true;

              default:
                return super.onOptionsItemSelected(item);
        }
    }


     public class CustomArrayListAdapter extends ArrayAdapter<String>  {
        public ArrayList<String> myItems;
        Context myContext;
        int layoutResourceId;

        /* here we must override the constructor for ArrayListAdapter
        * the only variable we care about now is ArrayList<Item> objects,
        * because it is the list of objects we want to display.
        */
        public CustomArrayListAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.myContext=context;
            this.myItems = objects;
            this.layoutResourceId = textViewResourceId;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // assign the view we are converting to a local variable
            View v = convertView;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)  this.myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(this.layoutResourceId, parent, false);
            }

            // get the EditView and then set the text (item name)
            EditText mEditText = (EditText) v.findViewById(android.R.id.text1);
            mEditText.setText(this.myItems.get(position));

            final int pos = position;


            //as in edit mode, we need to add onFocus listener and update adapter once we finish with editing
            mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final EditText et = (EditText) v;
                        final String t = et.getText().toString().trim();
                        listItemsArrayList.set(pos, t);

                    }
                }
            });



            return v;
        }
    }

}

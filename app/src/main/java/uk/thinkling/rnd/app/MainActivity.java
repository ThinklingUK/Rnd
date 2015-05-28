package uk.thinkling.rnd.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    ArrayList<String> stringArrayList;
    ArrayAdapter<String> listArrayAdapter;
    String[][] stringArray;
    int arrayNum=0;
    ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stringArray = new String[][] {{"I cannot say", "Definitely", "It is unlikely", "Ask again later", "Highly probable", "Against the odds", "I have doubts", "Perhaps tomorrow"},
                {"ONE","TWO","THREE","FOUR","FIVE","SIX"},{"YES","NO","MAYBE"},{"tea","wine","coffee","lager","ale","vodka","gin","bubbles"}};
        listArrayAdapter = new ArrayAdapter(this, R.layout.centred_list_layout, new ArrayList<String>(Arrays.asList(stringArray[arrayNum])));
        myListView = (ListView) findViewById(R.id.listView1);
        myListView.setAdapter(listArrayAdapter);
        // React to user clicks on item
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {

                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;
                Toast.makeText(MainActivity.this, "Item with id [" + id + "] - Position [" + position + "] - says [" + clickedView.getText() + "]", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch ( item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings: //
                return true;


            case R.id.action_next: //change to swipe left/right
                arrayNum++;
                if (arrayNum>=stringArray.length) arrayNum=0;
                //fallthrough to reset

            case R.id.action_reset:
                listArrayAdapter.clear();
                for(int i = 0; i < stringArray[arrayNum].length; i++) {
                    listArrayAdapter.add(stringArray[arrayNum][i]);
                }
                return true;

            case R.id.action_remove_one: //should just be a click on the screen
                if (listArrayAdapter.getCount() > 1) {
                    listArrayAdapter.remove(listArrayAdapter.getItem((int) (Math.random() * listArrayAdapter.getCount()))); // remove one element at random
                    listArrayAdapter.notifyDataSetChanged();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

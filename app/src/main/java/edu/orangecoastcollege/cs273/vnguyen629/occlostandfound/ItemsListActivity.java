package edu.orangecoastcollege.cs273.vnguyen629.occlostandfound;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static edu.orangecoastcollege.cs273.vnguyen629.occlostandfound.UserAccount.isLoggedIn;

/**
 * Displays a list of reported lost items to the user, where they can
 * either click on the item to view the specific details related to the item
 * or click on the report lost item button to report a lost item.
 *
 * @author Vincent Nguyen
 */
public class ItemsListActivity extends AppCompatActivity {
    private DBHelper database;
    private static List<Item> allItemsList;
    private static List<Item> filteredItemsList;
    private static ItemListAdapter itemsListAdapter;
    private ListView itemsListView;

    private Spinner categoryFilterSpinner;
    private EditText searchNameFilterEditText;

    private Sensor accelerometer;
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;

    private UserAccount account;

    /**
     * Loads up the database and ListView of all the reported items. Also sets up the
     * shake detector so if there's an appropriate shake, the ReportItemActivity
     * will be loaded.
     * @param savedInstanceState The state of the application saved into a bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        isLoggedIn = false;

        if (isLoggedIn)
            account = getIntent().getExtras().getParcelable("Account");

        database = new DBHelper(this);

        searchNameFilterEditText = (EditText) findViewById(R.id.searchNameFilterEditText);
        searchNameFilterEditText.addTextChangedListener(itemNameSearchTextWatcher);

        allItemsList = database.getAllItems();
        filteredItemsList = new ArrayList<>(allItemsList);

        categoryFilterSpinner = (Spinner) findViewById(R.id.categoryFilterSpinner);
        ArrayAdapter<String> categoryFilterSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getItemNames());
        categoryFilterSpinner.setAdapter(categoryFilterSpinnerAdapter);
        categoryFilterSpinner.setOnItemSelectedListener(categoryFilterSpinnerListener);

        itemsListView = (ListView) findViewById(R.id.itemsListView);
        itemsListAdapter = new ItemListAdapter(this, R.layout.list_item, filteredItemsList);
        itemsListView.setAdapter(itemsListAdapter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            /**
             * When a 3D motion that the sensors constitute as a shake has been detected,
             * the ItemListActivity is loaded
             */
            @Override
            public void onShake() {
                startActivity(new Intent(ItemsListActivity.this, ReportItemActivity.class));
            }
        });
    }

    /**
     * Retrieves all the <code>Item</code> names.
     * @return A string array of all the <code>Item</code> names.
     */
    private String[] getItemNames() {
        String categoryNames[] = new String[4];

        categoryNames[0] = getString(R.string.all_reported_items_text);
        categoryNames[1] = getString(R.string.my_reported_items);
        categoryNames[2] = getString(R.string.found_items_text);
        categoryNames[3] = getString(R.string.missing_items_text);

        return categoryNames;
    }

    /**
     * Monitors the EditText for searching item names in the database. It displays results
     * as the text is typed/deleted accordingly.
     */
    public TextWatcher itemNameSearchTextWatcher = new TextWatcher() {
        /**
         * Unused
         * @param charSequence Unused
         * @param i Unused
         * @param i1 Unused
         * @param i2 Unused
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        /**
         * Monitors the EditText for searching item names in the database. It displays results
         * as the text is typed/deleted accordingly.
         * @param charSequence The input from the EditText.
         * @param i Unused
         * @param i1 Unused
         * @param i2 Unused
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = charSequence.toString().toLowerCase();
            itemsListAdapter.clear();

            if (input.equals(""))
                for (Item item: allItemsList)
                    itemsListAdapter.add(item);
            else {
                String itemName;
                for (Item item : allItemsList) {
                    //itemName = String.valueOf(categoryFilterSpinner.getSelectedItem());
                    if (item.getName().toLowerCase().contains(input))
                        itemsListAdapter.add(item);
                }
            }
        }

        /**
         * Unused
         * @param editable Unused
         */
        @Override
        public void afterTextChanged(Editable editable) {}
    };

    /**
     * AdapterView
     * OnItemSelectedListener for item selected listener
     */
    public AdapterView.OnItemSelectedListener categoryFilterSpinnerListener =
            new AdapterView.OnItemSelectedListener() {
                /**
                 * If the user selects a valid category, it displays all the
                 * <code>Item/code> objects that meet that specified
                 * criteria.
                 * @param parent Unused
                 * @param view Unused
                 * @param position Unused
                 * @param l Unused
                 */
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                    String selectedCategory = String.valueOf(parent.getItemAtPosition(position));
                    itemsListAdapter.clear();

                    if (selectedCategory.equals(getString(R.string.all_reported_items_text)))
                        for (Item item : allItemsList)
                            itemsListAdapter.add(item);
                    else if (isLoggedIn) {
                        for (Item item : allItemsList) {
                            //if (item.getReportedUsername().equals(
                                    //UserAccount.singedInUserAccountName)) {
                                itemsListAdapter.add(item);
                            //}
                        }
                    }
                    else if (selectedCategory.equals(getString(R.string.found_items_text))) {
                        for (Item item : allItemsList)
                            if (item.getStatus())
                                itemsListAdapter.add(item);
                    }
                    else if (selectedCategory.equals(getString(R.string.missing_items_text))) {
                        for (Item item : allItemsList)
                            if (!item.getStatus())
                                itemsListAdapter.add(item);
                    }
                }
                /**
                 * If the user clicks on the category filter spinner but ends up not choosing a
                 * category, the default value is set.
                 * @param parent The category filter spinner
                 */
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    parent.setSelection(0);
                }
            };

    /**
     * Allows the user to click and choose a specific item from the database to view
     * in more detail, opening up the ItemDetailsActivity.
     * @param view The ItemList ListView displaying all the items in the database.
     */
    public void viewItemDetails(View view) {
        if (view instanceof LinearLayout) {
            final Item SELECTED_ITEM = (Item) view.getTag();
            Log.i("OCC Lost and Found", SELECTED_ITEM.toString());

            Intent detailsIntent = new Intent(this, ItemDetailsActivity.class);
            detailsIntent.putExtra("Selected", SELECTED_ITEM);
            startActivity(detailsIntent);
        }
    }

    /**
     * Clears all the current items inside the database.
     * @param view The widget to clear all the items inside the database.
     */
    public void clearAllItems(View view) {
        if (!allItemsList.isEmpty()) {
            database.deleteAllItems();
            allItemsList.clear();
            itemsListAdapter.notifyDataSetChanged();
        }
        else
            Toast.makeText(this, getString(R.string.list_empty_text), Toast.LENGTH_SHORT).show();
    }

    /**
     * Opens up the ReportItemActivity so the user may submit a report. If the user
     * is not signed in, they will not be able to report a lost item.
     * @param view The Button that loads up the ReportItemActivity.
     */
    public void reportLostItem(View view) {
        if (isLoggedIn)
            startActivity(new Intent(ItemsListActivity.this, ReportItemActivity.class));
        else
            Toast.makeText(this, getString(R.string.must_be_signed_in_text),
                    Toast.LENGTH_SHORT).show();
    }

    /**
     * Clears the item name search EditText and category filter spinner, resetting them
     * to default values.
     * @param view The reset filters Button.
     */
    public void resetFilters(View view) {
        searchNameFilterEditText.setText("");
        categoryFilterSpinner.setSelection(0);
    }

   /**
     * When the user re-enters the app, the sensors start back up and begin
     * monitoring device movements/g-forces in a 3D (x-y-z) span. Only loads up the
     * ReportActivity if the user is logged in.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedIn) {
            sensorManager.registerListener(shakeDetector, accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * When the user switches apps or clicks on the home button without closing the app,
     * all the sensors that monitor device movements and g-forces are then paused
     * to preserve battery life.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }

    /**
     * Get uri to any resource type within an Android Studio project. Method is public static to
     * allow other classes to use it as a helper function.
     * @param context The current context.
     * @param resID The resource identifier for drawable.
     * @return Uri to resource by given id.
     * @throws Resources.NotFoundException If the given resource id does not exist.
     */
    public static Uri getUriToResource(@NonNull Context context, @AnyRes int resID)
            throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /** return URI. */
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resID)
                + '/' + res.getResourceTypeName(resID)
                + '/' + res.getResourceEntryName(resID));
    }
}
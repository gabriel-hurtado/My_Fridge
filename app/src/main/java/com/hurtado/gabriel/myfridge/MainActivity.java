package com.hurtado.gabriel.myfridge;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    public static boolean finishedLoading=false;
    public static ListView foodListView;
    private static FragmentManager man;
    private static foodClient foodClientel;
    private static Context context;
    private static int hour;
    private static int min;
    private int order;
private static MenuItem itemM;
    private foodListAdapter adapter;
    private DbAdapter dbHelper;

    private  String date;
        private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



private static void clearAlarms(Context contextI) {


        SharedPreferences prefs = contextI.getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt("notificationNumber", 0);
        NotificationManager mNM = (NotificationManager) contextI.getSystemService(NOTIFICATION_SERVICE);
        // Send the notification to the system.
        mNM.cancelAll();

        for (int i = 0; i < notificationNumber; i++) {
            foodClient.resetAlarmForNotification(i,contextI);


        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("notificationNumber", 0);
        editor.apply();



        DbAdapter dbHelper = new DbAdapter(contextI);
        dbHelper.open();
        Cursor cursor = dbHelper.fetchAll();
        if (cursor != null && cursor.moveToFirst()) {


            do {


                String date = cursor.getString(2);
                if (Character.isDigit(date.charAt(0))) {
                    StringTokenizer tokens = new StringTokenizer(date, "/");
                    String first = tokens.nextToken();
                    String second = tokens.nextToken();
                    String third = tokens.nextToken();
                    third = third.replace(" ", "");

                    int yr = Integer.parseInt(third);
                    int monthOfYear = Integer.parseInt(second) - 1;
                    int dayOfMonth = Integer.parseInt(first);
                    Calendar alarm = Calendar.getInstance();


                    alarm.set(yr, monthOfYear, dayOfMonth);

                    alarm.add(Calendar.DAY_OF_MONTH, -1);
                    alarm.set(Calendar.HOUR_OF_DAY, hour);
                    alarm.set(Calendar.MINUTE, min);
                    alarm.set(Calendar.SECOND, 0);
                    long id = Long.parseLong(cursor.getString(0));
                    foodClient.setAlarmForNotification(alarm, id);
                }
            } while (cursor.moveToNext());

        }
    }

public static void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(man, "datePicker");

    }
        public void onDateSet(DatePicker view, int yr, int monthOfYear,
                              int dayOfMonth) {

            String dateSet = String.valueOf(dayOfMonth) + "/"+String.valueOf(monthOfYear + 1) + "/"
                    + String.valueOf(yr) + " ";
            adapter.setDateSet(dateSet);

            Calendar alarmDate = Calendar.getInstance();


            alarmDate.set(yr, monthOfYear, dayOfMonth, 0, 0);

            alarmDate.add(Calendar.DAY_OF_MONTH, -1);
            alarmDate.set(Calendar.HOUR_OF_DAY, hour);
            alarmDate.set(Calendar.MINUTE, min);
            alarmDate.set(Calendar.SECOND, 0);

           clearAlarms(context);

        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        order=0;
        super.onCreate(savedInstanceState);
        MainActivity.context=getApplicationContext();
        date  =  getString(R.string.Expire);
        man = getFragmentManager();

        foodClientel = new foodClient(this);
        foodClientel.doBindService();
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        hour = prefs.getInt("hour", 10);
        min = prefs.getInt("min", 0);



        dbHelper = new DbAdapter(this);
        dbHelper.open();
        Cursor cursor = dbHelper.fetchAll();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupUI(findViewById(R.id.toolbar));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.insert(new foodItem("", date), 0);
                Snackbar.make(view, R.string.ItemAdded, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setupUI(findViewById(R.id.drawer_layout));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu=navigationView.getMenu();

        MenuItem itemCreate=menu.getItem(1);
        NumberFormat f = new DecimalFormat("00");
        itemCreate.setTitle(getString(R.string.Notificate) + hour + ":" + f.format(min) );

/*
        MenuItem item = menu.getItem(2);


        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();*/

        navigationView.setNavigationItemSelectedListener(this);


        setupUI(findViewById(R.id.nav_view));


        setupListViewAdapter();

        if (cursor != null && cursor.moveToFirst()) {


            do {

                String name = cursor.getString(1);
                String date = cursor.getString(2);
                 foodItem items= new foodItem(name, date);
                items.setPosition(Long.parseLong(cursor.getString(0)));
                adapter.insert(items, 0);


            } while (cursor.moveToNext());
        }
        dbHelper.close();



        finishedLoading= true;

    }

    private void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }}

public void removeOnClickHandler(View v) {
        foodItem itemToRemove = (foodItem) v.getTag();
        long pos = itemToRemove.getPosition();
        adapter.remove(itemToRemove);
        dbHelper = new DbAdapter(this);
        dbHelper.open();
        dbHelper.delete(pos);
        dbHelper.close();
        hideSoftKeyboard(MainActivity.this);
        clearAlarms(context);
        Snackbar.make(v, R.string.Deleted, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        dbHelper.close();
    }

        private void setupListViewAdapter() {
        adapter = new foodListAdapter(MainActivity.this, new ArrayList<foodItem>());
        foodListView = (ListView)findViewById(R.id.food_list);
        foodListView.setAdapter(adapter);


    }
@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        itemM=item;

        if (id == R.id.nav_reset) {
            dbHelper = new DbAdapter(this);
            dbHelper.open();
            dbHelper.deleteAll();
            dbHelper.close();
            adapter.clear();


            SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
            int notificationNumber = prefs.getInt("notificationNumber", 0);
            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Send the notification to the system.
            mNM.cancelAll();

            for(int i=0;i<=notificationNumber;i++) {
                foodClient.resetAlarmForNotification(i, null);


            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("notificationNumber", 0);
            editor.apply();
        } else if (id == R.id.nav_manage) {


            int mHour = hour;
            int mMinute = min;

            // Launch Time Picker Dialog
            TimePickerDialog tpd;
            tpd = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            NumberFormat f = new DecimalFormat("00");
                            itemM.setTitle(getString(R.string.Notificate) + hourOfDay + ":" + f.format(minute) );
                            hour=hourOfDay;
                            min=minute;

                            SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("hour", hour);
                            editor.putInt("min", min);
                            editor.apply();
                            clearAlarms(context);
                        }
                    }, mHour, mMinute, true);
            tpd.show();


        } else if (id == R.id.nav_share) {
            share();

        }

        else if (id == R.id.nav_sort) {

            ArrayList<String> previous;

            adapter.clear();
            dbHelper = new DbAdapter(this);

                order=1;

            previous= new ArrayList<>();

            dbHelper.open();
            Cursor cursor = dbHelper.fetchAll();

            if (cursor != null && cursor.moveToFirst()) {


                do {

                    String name = cursor.getString(1);
                    String dateRestore = cursor.getString(2);
                    foodItem items= new foodItem(name, dateRestore);
                    items.setPosition(Long.parseLong(cursor.getString(0)));
                    adapter.insert(items, getPosit(dateRestore, previous));


                } while (cursor.moveToNext());
            }
            order=0;
            dbHelper.close();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    int getPosit(String date, ArrayList previous){

        int i;
        if(order==0)
        {
            return 0;
        }

         if (!(Character.isDigit(date.charAt(0)))) {
            previous.add(0,date);
            return 0;
        }
         if(previous.size()==0)
        {
            previous.add(0,date);
            return 0;
        }
        else{
        for(i=0;i<previous.size();i++){
            if(prev(date, (String) previous.get(i)))
            {
                previous.add(i,date);
                return i;
            }
        }
             previous.add(i,date);
            return i;
    }

    }

    private boolean prev(String date, String prev) {
        if (!(Character.isDigit(prev.charAt(0)))) {
            return false;
        }
        Calendar cur = tokenize(date);
        Calendar pre = tokenize(prev);

        if(cur.before(pre)){
            return true;
        }

        return false;
    }

    private Calendar tokenize(String date) {
        StringTokenizer tokens = new StringTokenizer(date, "/");
        String first = tokens.nextToken();
        String second = tokens.nextToken();
        String third = tokens.nextToken();
        third = third.replace(" ", "");

        int yr = Integer.parseInt(third);
        int monthOfYear = Integer.parseInt(second);
        int dayOfMonth = Integer.parseInt(first);
        Calendar alarm = Calendar.getInstance();


        alarm.set(yr, monthOfYear, dayOfMonth);
        return alarm;
    }

    private void share() {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            String text=getString(R.string.Sharer);
            // Add data to the intent, the receiving app will decide
                // what to do with it.
            DbAdapter dbHelper = new DbAdapter(context);
            dbHelper.open();
            Cursor cursor = dbHelper.fetchAll();
            if (cursor != null && cursor.moveToFirst()) {

                do {


                    String food = cursor.getString(1);
                    text=text+"\n"+food;

                } while (cursor.moveToNext());

            }
            share.putExtra(Intent.EXTRA_SUBJECT, "My Fridge");
            share.putExtra(Intent.EXTRA_TEXT, text);

            startActivity(Intent.createChooser(share, getString(R.string.ShareTitle)));

    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(foodClientel != null)
            foodClientel.doUnbindService();
        super.onStop();
    }


}

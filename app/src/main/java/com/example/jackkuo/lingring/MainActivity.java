package com.example.jackkuo.lingring;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public com.example.jackkuo.lingring.IncomingCallReceiver callReceiver;
    public Dial DialObj;
    public List<Integer> fragmentID = new ArrayList<>();
    public List<Fragment> fragmentList = new ArrayList<>();
    public Integer lastID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //when application starts this fragment will be displayed
        setTitle("Call");
        Dial fragment = new Dial();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fram, fragment, "Call");
        fragmentTransaction.commit();
        fragmentID.add(R.id.nav_reg);
        fragmentList.add(fragment);
        lastID = R.id.nav_reg;
        //register a broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.jackkuo.lingring.INCOMING_CALL");
        callReceiver = new com.example.jackkuo.lingring.IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);

    }

    @Override
    public void onResume(){
        super.onResume();
        //這不能放在 onCreate 因為可能還沒建立好該 fragment
        DialObj = (Dial) this.getSupportFragmentManager().findFragmentByTag("Call");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment to, int id) {

        Fragment from = fragmentList.get(fragmentID.indexOf(lastID));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Log.d("fragmentID", "from: "+ lastID);
        if (fragmentID.indexOf(id) < 0) {    // 判斷先前是否被 add 過
            Log.d("fragmentID", "id: "+ id);
            Log.d("add", "y");
            fragmentID.add(id);
            fragmentList.add(to);
            fragmentTransaction.hide(from).add(R.id.fram, to).commit(); // 隱藏目前的 fragment，add 下一個到 Activity 中
        } else {
            to = fragmentList.get(fragmentID.indexOf(id));
            fragmentTransaction.hide(from).show(to).commit(); // 隱藏目前的 fragment，顯示下一個
            Log.d("add", "n");
        }
        lastID = id;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reg) {

            setTitle("Call");
            Dial fragment = new Dial();
            switchContent(fragment, id);

        } else if (id == R.id.nav_contact) {

            setTitle("Contacts");
            Contacts fragment = new Contacts();
            switchContent(fragment, id);

        } else if (id == R.id.nav_about) {

            setTitle("About");
            About fragment = new About();
            switchContent(fragment, id);

        } else if (id == R.id.nav_settings) {

            setTitle("Settings");
            Settings fragment = new Settings();
            switchContent(fragment, id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 給 Contact 呼叫的函數
    public void call(String ContactPhoneNum){
        DialObj.ContactCalling(ContactPhoneNum);
    }

    public void initializeManager(){
        DialObj.initializeManager();
    }
}

package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import pt.andred.cmu1617.LocMessAPIClientImpl;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // To  identify click on the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    private void selectItem(int position) {
        switch(position) {
            case 1:
//                Intent a = new Intent(MainActivity.this, Activity1.class);
//                startActivity(a);
                break;
            case 2:
//                Intent b = new Intent(MainActivity.this, Activity2.class);
//                startActivity(b);
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            default:
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.settings:
                Log.wtf("TAG", "settings pressed");
//                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.profile:
                Log.wtf("TAG", "profile pressed");

//                Intent intent = new Intent(this, SelfProfileActivity.class);
//                intent.putExtra("user_id", _profile.userid());
//                intent.putExtra("local", _profile.isLocal());
//                intent.putExtra("self", true);
//
//                startActivity(intent);
                break;
            case R.id.logout:
                Log.wtf("TAG", "logout pressed");
                LocMessAPIClientImpl.getInstance().logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

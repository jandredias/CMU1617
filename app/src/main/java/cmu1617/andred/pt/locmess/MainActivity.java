package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import pt.andred.cmu1617.LocMessAPIClientImpl;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
//    private TextView mTextMessage;
    ViewPager _mainViewPager;
    FragmentPagerAdapter _adapterViewPager;
    private SQLDataStoreHelper _db;
    private Fragment _main_fragment;
    private FragmentManager _fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        _db = new SQLDataStoreHelper(this);
        _main_fragment = new DualLocationsFragment();
        final FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, _main_fragment).commit();

        // To  identify click on the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             //For left menu appear
             @Override
             public boolean onNavigationItemSelected(MenuItem item) {
                 // Handle navigation view item clicks here.

                 switch (item.getItemId()) {
                     case R.id.settings:
                         Log.wtf(TAG, "settings pressed");
//                                                                         startActivity(new Intent(this, SettingsActivity.class));
                         break;
                     case R.id.profile:
                         Log.wtf(TAG, "profile pressed");

                         Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                         startActivity(intent);
                         break;
                     case R.id.logout:
                         Log.wtf(TAG, "logout pressed");
                         LocMessAPIClientImpl.getInstance().logout();
                         new Login(_db).logout();
                         startActivity(new Intent(MainActivity.this, LoginActivity.class));
                         break;
                 }

                 DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                 drawer.closeDrawer(GravityCompat.START);
                 return true;
             }
         });

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.three_buttom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.navigation_locations:
                        _main_fragment = new DualLocationsFragment();
                        break;
                    case R.id.navigation_dashboard:
                        _main_fragment = new DashboardFragment();
                        break;
                    case R.id.navigation_archive:
                        _main_fragment = new ArchiveFragment();
                        break;
                }
                final FragmentTransaction transaction = _fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, _main_fragment).commit();
                return true;
            }
        });
    }
}

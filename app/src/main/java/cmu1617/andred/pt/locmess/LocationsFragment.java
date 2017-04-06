package cmu1617.andred.pt.locmess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by miguel on 06/04/17.
 */

public class LocationsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private static final String TAG = "LocationsFragment";
    private SQLDataStoreHelper _dbHelper;
    private ViewPager _mainViewPager;
    private View _tab_outer0;
    private View _tab_outer1;
    private View _underline0;
    private View _underline1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _dbHelper = new SQLDataStoreHelper(getContext());



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _dbHelper.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.locations, container, false);
        _mainViewPager = (ViewPager) view.findViewById(R.id.locations_pager);
        MainPageAdapter adapterViewPager = new MainPageAdapter(getChildFragmentManager());
        _mainViewPager.setAdapter(adapterViewPager);
        _mainViewPager.addOnPageChangeListener(this);
        _mainViewPager.setCurrentItem(0);

        _tab_outer1 = view.findViewById(R.id.tab_outer_1);
        _tab_outer0 = view.findViewById(R.id.tab_outer_0);
        _underline0 = view.findViewById(R.id.underline_0);
        _underline1 = view.findViewById(R.id.underline_1);

        _tab_outer0.setOnClickListener(this);
        _tab_outer1.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_outer_0:
                _mainViewPager.setCurrentItem(0);
                break;
            case R.id.tab_outer_1:
                _mainViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                _underline0.setAlpha(1);
                _tab_outer0.setAlpha(1);
                _underline1.setAlpha(0);
                _tab_outer1.setAlpha((float) 0.6);
                break;
            case 1:
                _underline0.setAlpha(0);
                _tab_outer0.setAlpha((float) 0.6);
                _underline1.setAlpha(1);
                _tab_outer1.setAlpha(1);

                break;
        }
    }
    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MainPageAdapter extends FragmentPagerAdapter {
        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WifiLocationsFragment();
                case 1:
                    return new GPSLocationsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}

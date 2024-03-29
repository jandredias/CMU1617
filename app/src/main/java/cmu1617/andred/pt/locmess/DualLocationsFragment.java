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

public class DualLocationsFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private static final String TAG = "DualLocationsFragment";
    private View _tab_outer0;
    private View _tab_outer1;
    private View _underline0;
    private View _underline1;
    private ViewPager _mainViewPager;





    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        _mainViewPager = (ViewPager) view.findViewById(R.id.locations_pager);
        MainPageAdapter adapterViewPager = new MainPageAdapter(getChildFragmentManager());
        _mainViewPager.setAdapter(adapterViewPager);
        _mainViewPager.addOnPageChangeListener(this);
        _mainViewPager.setCurrentItem(0);

        _tab_outer1 = view.findViewById(R.id.tab_outer_1);
        _tab_outer0 = view.findViewById(R.id.tab_outer_0);
        _underline0 = view.findViewById(R.id.underline_0);
        _underline1 = view.findViewById(R.id.underline_1);

        ViewClickListener viewListener = new ViewClickListener();

        _tab_outer0.setOnClickListener(viewListener);
        _tab_outer1.setOnClickListener(viewListener);

        return view;
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
                    return new GPSLocationsFragment();
                case 1:
                    return new WifiLocationsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    public class ViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_0:
                case R.id.tab_outer_0:
                    _mainViewPager.setCurrentItem(0);
                    break;
                case R.id.text_1:
                case R.id.tab_outer_1:
                    _mainViewPager.setCurrentItem(1);
                    break;
            }
        }
    }

}

package com.multazamgsd.takin.ui.home;

import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.multazamgsd.takin.util.ShadowTransformer;

import java.util.ArrayList;
import java.util.List;

public class SlideshowPagerAdapter extends FragmentStatePagerAdapter implements ShadowTransformer.CardAdapter {

    private List<OneSlideshowFragment> fragments;
    private float baseElevation;

    public SlideshowPagerAdapter(FragmentManager fm, float baseElevation) {
        super(fm);
        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;

        for(int i = 0; i< 8; i++){
            addCardFragment(new OneSlideshowFragment());
        }
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return OneSlideshowFragment.getInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (OneSlideshowFragment) fragment);
        return fragment;
    }

    public void addCardFragment(OneSlideshowFragment fragment) {
        fragments.add(fragment);
    }

}

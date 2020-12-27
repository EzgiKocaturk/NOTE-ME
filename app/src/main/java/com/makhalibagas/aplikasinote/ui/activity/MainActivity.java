package com.makhalibagas.aplikasinote.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.makhalibagas.aplikasinote.R;
import com.makhalibagas.aplikasinote.ui.fragment.TaskFragment;
import com.makhalibagas.aplikasinote.ui.fragment.NotesFragment;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager2 = findViewById(R.id.vp2);
        TabLayout tabLayout = findViewById(R.id.tab);
        viewPager2.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.all);
                        break;
                    case 1:
                        tab.setText(R.string.collections);
                        break;
                    default:
                        break;
                }
            }
        }).attach();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    static class ViewPagerAdapter extends FragmentStateAdapter {

        ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity){
            super(fragmentActivity);
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new NotesFragment();
                case 1:
                    return new TaskFragment();
            }
            return new NotesFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}

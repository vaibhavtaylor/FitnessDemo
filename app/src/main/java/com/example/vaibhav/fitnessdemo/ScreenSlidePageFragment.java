package com.example.vaibhav.fitnessdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vaibhav on 30-09-2017.
 */

public class ScreenSlidePageFragment extends Fragment {

    private String[] taskCategory = {"Running", "Cycling", "Weight Lifting", "Stretching"};
    private int[] imageId = { R.drawable.ic_running, R.drawable.ic_bike, R.drawable.ic_weightlifting, R.drawable.ic_stretching_exercises };
    private TextView taskName;
    private ImageView taskImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        //setting the view with current page value
        taskName = (TextView) rootView.findViewById(R.id.taskName);
        taskName.setText(taskCategory[getArguments().getInt("position")]);

        taskImg = (ImageView) rootView.findViewById(R.id.taskImg);
        taskImg.setBackgroundResource(imageId[getArguments().getInt("position")]);

        return rootView;
    }

    public static ScreenSlidePageFragment newInstance(int position) {

        //depending on current view we set the position of the page
        ScreenSlidePageFragment f = new ScreenSlidePageFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);

        f.setArguments(b);

        return f;
    }

}

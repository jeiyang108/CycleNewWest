package com.project.lyt.cyclenewwest.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.lyt.cyclenewwest.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrailsFragment extends Fragment {
    public TrailsFragment() {
        // Required empty public constructor
    }

    //after view is created
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);



    }
    //while view is being created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trails, container, false);

    }



}

package com.project.lyt.cyclenewwest.Activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.lyt.cyclenewwest.Manager.BikeWayManager;
import com.project.lyt.cyclenewwest.Model.GreenWay;
import com.project.lyt.cyclenewwest.R;
import com.project.lyt.cyclenewwest.Adapter.TrailAdapter;

import java.util.ArrayList;

/**
 * Activity for favourite trails (greenways).
 */
public class FavouritesActivity extends AppCompatActivity {
    private ArrayList<GreenWay> lstFav;
    private ListView lv;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lstFav = MainActivity.getFavs();

        lv = (ListView) findViewById(R.id.lst_fav);
        TrailAdapter adapter = new TrailAdapter(FavouritesActivity.this, lstFav);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textName = (TextView) view.findViewById(R.id.itemName);
                MainActivity.showBikeWay(BikeWayManager.getGreenWayByName(textName.getText().toString()));

                finish();
            }
        });
    }
}

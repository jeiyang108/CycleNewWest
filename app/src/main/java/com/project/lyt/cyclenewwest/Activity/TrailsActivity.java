package com.project.lyt.cyclenewwest.Activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.project.lyt.cyclenewwest.Manager.BikeWayManager;
import com.project.lyt.cyclenewwest.Model.GreenWay;
import com.project.lyt.cyclenewwest.R;
import com.project.lyt.cyclenewwest.Adapter.TrailAdapter;

import java.util.ArrayList;

/**
 * Activity showing list of trails (greenways).
 */
public class TrailsActivity extends AppCompatActivity {
    private ArrayList<GreenWay> lstGreenWay;

    private ListView lv;
    private EditText edtName;
    private Spinner spnLength;
    private Button btnSearch;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lstGreenWay = BikeWayManager.getAllGreenways();

        lv = (ListView) findViewById(R.id.lst_trails_results);
        edtName = (EditText) findViewById(R.id.input_trails_name);
        spnLength = (Spinner) findViewById(R.id.spn_length);
        btnSearch = (Button) findViewById(R.id.btn_search);

        TrailAdapter adapter = new TrailAdapter(TrailsActivity.this, lstGreenWay);

        // Attach the adapter to a ListView
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textName = (TextView) view.findViewById(R.id.itemName);
                MainActivity.showBikeWay(BikeWayManager.getGreenWayByName(textName.getText().toString()));
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().toLowerCase();
                String length = spnLength.getSelectedItem().toString();

                loadResults(name, length);
            }
        });
    }

    /**
     * Loads list of GreenWays based on search parameter.
     * @param name
     *      The partial name string searched for.
     * @param length
     *      The selected length.
     */
    public void loadResults(String name, String length) {
        ArrayList<GreenWay> lstResults = new ArrayList<GreenWay>();

        for(int i = 0; i < lstGreenWay.size(); i++) {
            if(lstGreenWay.get(i).getFullName().toLowerCase().contains(name)) {
                switch(length) {
                    case "0 - 2 km":
                        if(lstGreenWay.get(i).getKM() <= 2) {
                            lstResults.add(lstGreenWay.get(i));
                        }

                        break;
                    case "2 - 4 km":
                        if(lstGreenWay.get(i).getKM() >= 2 && lstGreenWay.get(i).getKM() <= 4) {
                            lstResults.add(lstGreenWay.get(i));
                        }

                        break;
                    case "4 - 6 km":
                        if(lstGreenWay.get(i).getKM() >= 4 && lstGreenWay.get(i).getKM() <= 6) {
                            lstResults.add(lstGreenWay.get(i));
                        }

                        break;
                    case "6 - 8 km":
                        if(lstGreenWay.get(i).getKM() >= 6 && lstGreenWay.get(i).getKM() <= 8) {
                            lstResults.add(lstGreenWay.get(i));
                        }

                        break;
                    case "8+ km":
                        if(lstGreenWay.get(i).getKM() >= 8) {
                            lstResults.add(lstGreenWay.get(i));
                        }

                        break;
                    default:
                        lstResults.add(lstGreenWay.get(i));
                }
            }
        }

        TrailAdapter adapter = new TrailAdapter(TrailsActivity.this, lstResults);
        lv.setAdapter(adapter);
    }
}

package com.project.lyt.cyclenewwest.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.lyt.cyclenewwest.R;

/**
 * Splash page displayed when first opening the program.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView gif = (ImageView) findViewById(R.id.loading_bike);
        Glide.with(this).asGif().load(R.raw.loading).into(gif);
        Button btn = (Button) findViewById(R.id.moveToMain);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
            }
        });
    }
}
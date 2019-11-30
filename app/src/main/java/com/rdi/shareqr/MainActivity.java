package com.rdi.shareqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rdi.shareqr.generator.GeneratorHost;


public class MainActivity extends AppCompatActivity implements GeneratorHost {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyDialog()
//                .build()
//        );

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout,
                            new InputFragment())
                    .commit();
        }

    }

    @Override
    public void proceedToGeneration(String source) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.slide_out_right)
                .replace(R.id.root_layout, QrFragment.newInstance(source))
                .commit();
    }
}

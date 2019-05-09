package com.arutech.sargam.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arutech.sargam.BuildConfig;
import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;

public class AboutActivity extends BaseActivity implements View.OnClickListener {



    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SargamApplication.getComponent(this).inject(this);

        ((TextView) findViewById(R.id.aboutVersion)).setText(BuildConfig.VERSION_NAME);

        ((ImageView) findViewById(R.id.aboutAppIcon)).setImageResource(R.mipmap.ic_launcher);

    }


    @Override
    public void onClick(View v) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(""));
        startActivity(webIntent);
    }
}

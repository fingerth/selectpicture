package com.fingerth.sp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fingerth.selectpic.SelectPictureBaseActivity;
import com.fingerth.selectpic.utils.permission.PermissionUtils;

import java.io.File;

public class MainActivity extends SelectPictureBaseActivity {
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionUtils.getPersimmions(this);
        iv = findViewById(R.id.iv);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPictureDialog();
            }
        });
    }

    @Override
    public void onCamera(File imageFile) {
        iv.setImageURI(Uri.parse(imageFile.getAbsolutePath()));
    }

    @Override
    public void onLocal(File imageFile) {
        iv.setImageURI(Uri.parse(imageFile.getAbsolutePath()));
    }
}

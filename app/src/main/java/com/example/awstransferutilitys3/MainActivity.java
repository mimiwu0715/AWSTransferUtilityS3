package com.example.awstransferutilitys3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnDownload;
    private Button btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }
    private void initUI() {
        btnDownload = findViewById(R.id.buttonDownloadMain);
        btnUpload = findViewById(R.id.buttonUploadMain);

        btnDownload.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DownloadActivity.class)));
        btnUpload.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, UploadActivity.class)));
    }
}
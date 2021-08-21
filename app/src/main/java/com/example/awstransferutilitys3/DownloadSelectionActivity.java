package com.example.awstransferutilitys3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadSelectionActivity extends ListActivity {
    private Util util;
    private AmazonS3Client s3;
    private String bucket;
    private SimpleAdapter simpleAdapter;
    private ArrayList<HashMap<String, Object>> transferRecordMaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_selection);
        util = new Util();
        bucket = new AWSConfiguration(this).optJsonObject("S3TransferUtility").optString("Bucket");
        initData();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the file list.
        new GetFileListTask().execute();
    }

    private void initData() {
        // Gets the default S3 client.
        s3 = util.getS3Client(DownloadSelectionActivity.this);
        transferRecordMaps = new ArrayList<>();
    }
    private void initUI() {
        simpleAdapter = new SimpleAdapter(this, transferRecordMaps,
                R.layout.bucket_item, new String[] {
                "key"
        },
                new int[] {
                        R.id.key
                });
        simpleAdapter.setViewBinder((view, data, textRepresentation) -> {
            if (view.getId() == R.id.key) {
                TextView fileName = (TextView) view;
                fileName.setText(data.toString());
                return true;
            }
            return false;
        });
        setListAdapter(simpleAdapter);

        // When an item is selected, finish the activity and pass back the S3
        // key associated with the object selected
        getListView().setOnItemClickListener((adapterView, view, pos, id) -> {
            Intent intent = new Intent();
            intent.putExtra("key", (String) transferRecordMaps.get(pos).get("key"));
            setResult(RESULT_OK, intent);
            finish();
        });
    }
    private class GetFileListTask extends AsyncTask<Void, Void, Void> {
        // A dialog to let the user know we are retrieving the files
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DownloadSelectionActivity.this,
                    getString(R.string.refreshing),
                    getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            // Queries files in the bucket from S3.
            List<S3ObjectSummary> s3ObjList = s3.listObjects(bucket).getObjectSummaries();
            transferRecordMaps.clear();
            for (S3ObjectSummary summary : s3ObjList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("key", summary.getKey());
                transferRecordMaps.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            simpleAdapter.notifyDataSetChanged();
        }
    }
}
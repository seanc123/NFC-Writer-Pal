package com.apps.seanc.nfcwriterpal.ArrayAdapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apps.seanc.nfcwriterpal.R;

import org.ndeftools.Record;

import java.util.List;

/**
 * Created by seanc on 30/03/2017.
 *
 * This array adapter is used for populating a ListView with information regarding the records read
 * from any tags scanned in the Read activity
 */


public class RecordAdapter extends ArrayAdapter<Record> {

    private List<Record> recordList = null;
    private Context context;
    public String TAG = RecordAdapter.class.getName();

    public RecordAdapter(Context context, int layoutId, List<Record> recordList){
        super(context, layoutId, recordList);
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return ((null != recordList) ? recordList.size() : 0);
    }

    @Override
    public Record getItem(int position) {
        return ((null != recordList) ? recordList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_record_list, null);
        }

        Record record = recordList.get(position);
        if (null != record) {
            TextView recordNo = (TextView) view.findViewById(R.id.record_no);
            TextView recordSize = (TextView) view.findViewById(R.id.record_size);
            TextView recordType = (TextView) view.findViewById(R.id.record_type);

            recordNo.setText(Integer.toString(position));
            recordSize.setText(Integer.toString(record.toByteArray().length) + " " + context.getString(R.string.bytes));
            recordType.setText(record.getClass().getSimpleName());

        }
        return view;
    }
}

package com.example.cbsgwsandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cbsgwsandbox.util.DatabaseManager;

public class ReplicationActivity extends AppCompatActivity {

    private DatabaseManager dbmgr;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbmgr = DatabaseManager.getSharedInstance();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replication);

        Spinner dropdown = findViewById(R.id.spnr_events);
        String[] items = new String[]{"5", "10", "20", "50", "100", "1000"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(dbmgr.replicationEvents.getSelectedSize());
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //dbmgr.replicationEvents.resize(Integer.parseInt(
                //        (String) parent.getItemAtPosition(position)
                //));
                dbmgr.replicationEvents.setSelectedSize(position);
                //System.out.println(dbmgr.replicationEvents);
                buildEventView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        Switch mySwitch = (Switch) findViewById(R.id.switch_repl);
        mySwitch.setChecked(dbmgr.isReplication());

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    dbmgr.startPushAndPullReplicationForCurrentUser(username,password);
                    buildEventView();
                } else {
                    dbmgr.stopAllReplicationForCurrentUser();
                    buildEventView();
                }
            }
        });

        buildEventView();
    }

    private void buildEventView() {
        TableLayout tl = (TableLayout) findViewById(R.id.tl_events);
        tl.removeAllViews();

        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView header = new TextView(this);
        header.setText("Replication Events");
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        headerRow.addView(header);
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        int eventSize = Integer.parseInt(((Spinner) findViewById(R.id.spnr_events)).getSelectedItem().toString());
        int displaySize =  (dbmgr.replicationEvents.getSize() < eventSize) ? dbmgr.replicationEvents.getSize() : eventSize;

        int getLoc = dbmgr.replicationEvents.getWriteloc();

        for (int i = 0; i < displaySize; i++ ) {

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView eventRow = new TextView(this);
            eventRow.setText(dbmgr.replicationEvents.read(getLoc));
            eventRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tr.addView(eventRow);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            getLoc--;
            if (getLoc < 0) { getLoc = dbmgr.replicationEvents.getCapacity()-1; }
        }
    }

    public void backMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
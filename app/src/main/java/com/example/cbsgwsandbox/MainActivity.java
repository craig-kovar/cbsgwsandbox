package com.example.cbsgwsandbox;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cbsgwsandbox.util.DatabaseManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static String username = "sandbox";
    private static String password = "password";
    public static final String DOC_KEY = "DOCID";
    private static String docId;

    EditText editTextDocID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Entered into onCreate");
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance();

        Context context = getApplicationContext();
        dbMgr.initializeCouchbaseLite(context);
        dbMgr.openOrCreateDatabaseForUser(context,username);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        docId = intent.getStringExtra(MainActivity.DOC_KEY);

        if (docId == null) {
            docId = "Enter Document ID";
        }

        editTextDocID = (EditText) findViewById(R.id.editTextDocID);
        if (editTextDocID != null) {
            editTextDocID.setText(docId);
        }
    }

    public void viewDocument(View view) {
        // Do something in response to button view Document
        System.out.println("[CKTEST] Testing view document button");

        Intent intent = new Intent(this, ViewDocument.class);
        editTextDocID = (EditText) findViewById(R.id.editTextDocID);
        String docId = editTextDocID.getText().toString();
        intent.putExtra(DOC_KEY, docId);
        startActivity(intent);

    }

    public void createDocument(View view) {
        // Do something in response to button view Document
        System.out.println("[CKTEST] Testing create document button");
        Intent intent = new Intent(this, CreateDocument.class);
        editTextDocID = (EditText) findViewById(R.id.editTextDocID);
        String docId = editTextDocID.getText().toString();
        intent.putExtra(DOC_KEY, docId);
        startActivity(intent);
    }
}
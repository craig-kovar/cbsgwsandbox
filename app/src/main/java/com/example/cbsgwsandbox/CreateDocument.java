package com.example.cbsgwsandbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.example.cbsgwsandbox.util.DatabaseManager;

public class CreateDocument extends AppCompatActivity {

    private static DatabaseManager dbmgr;
    private static String docId;
    private static String saved = "Document \"%s\" succesfully saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doc);

        dbmgr = DatabaseManager.getSharedInstance();
        Intent intent = getIntent();
        docId = intent.getStringExtra(MainActivity.DOC_KEY);
    }

    public void addRow(View view) {
        System.out.println("[CKTEST] Testing addRow");

        /* Find Tablelayout defined in main.xml */
        TableLayout tl = (TableLayout) findViewById(R.id.DocAttributes);
        /* Create a new row to be added. */
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        /* Create a Button to be the row-content. */

        EditText newKey = new EditText(this);
        newKey.setText("New Key");
        newKey.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText newValue = new EditText(this);
        newValue.setText("New Value");
        newValue.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        tr.addView(newKey);
        tr.addView(newValue);
        /* Add row to TableLayout. */
//tr.setBackgroundResource(R.drawable.sf_gradient_03);
        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void saveDocument(View view) {
        System.out.println("[CKTEST] Testing save document");

        Database db = dbmgr.getDatabase();
        TableLayout tl = (TableLayout) findViewById(R.id.DocAttributes);
        MutableDocument newDoc = new MutableDocument(docId);

        for(int i = 1, j = tl.getChildCount(); i < j; i++) {
            View tmpView = tl.getChildAt(i);
            if (tmpView instanceof TableRow) {
                // then, you can remove the the row you want...
                // for instance...
                TableRow row = (TableRow) tmpView;
                String tmpKey = ((TextView) row.getChildAt(0)).getText().toString();
                String tmpValue = ((TextView) row.getChildAt(1)).getText().toString();
                //System.out.println(String.format("\"%s\" : \"%s\"", tmpKey, tmpValue));
                newDoc.setString(tmpKey,tmpValue);
            } //End instanceof check
        } //End For Loop

        try {
            db.save(newDoc);
            //Toast toast = new Toast(getApplicationContext());
            Toast toast = Toast.makeText(this, String.format(saved,docId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } finally {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.DOC_KEY, docId);
            startActivity(intent);
        }

    }
}
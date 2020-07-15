package com.example.cbsgwsandbox;

import androidx.appcompat.app.AppCompatActivity;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.Where;
import com.example.cbsgwsandbox.util.DatabaseManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ViewDocument extends AppCompatActivity {

    private static final String docMetaLabel = "Document Metadata - %s";
    private static final String deleted = "Document \"%s\" deleted successfully";
    private static final String purged = "Document \"%s\" purged successfully";
    private static final String expired = "Document \"%s\" had expiry successfully set";
    private static DatabaseManager dbmgr;
    private static String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("[CKTEST] - ViewDocument:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        dbmgr = DatabaseManager.getSharedInstance();

        Intent intent = getIntent();
        docId = intent.getStringExtra(MainActivity.DOC_KEY);

        TextView metaView = (TextView) findViewById(R.id.label_meta);
        metaView.setText(String.format(docMetaLabel, docId));

        TextView textView = (TextView) findViewById(R.id.docView);
        textView.setText(buildDocView(docId));

        checkMetaStatus(docId);

    }

    private String buildDocView(String docId) {
        System.out.println("[CKTEST] ViewDocument:buildDocView");

        Database db = dbmgr.getDatabase();

        String retVal = String.format("Document \"%s\" not found", docId);
        String propertyString = "\t\"%s\" : \"%s\",\n";

        Document doc = db.getDocument(docId);
        if (doc != null) {
            retVal = "{\n";
            HashMap<String,Object> docValues = (HashMap<String,Object>) doc.toMap();
            for (String key : docValues.keySet()) {
                retVal = retVal + String.format(propertyString, key, docValues.get(key));
            }

            retVal = retVal.substring(0, retVal.length()-2);
            retVal = retVal + "\n}\n";

        }

        return retVal;
    }

    private void checkMetaStatus(String docId) {
        System.out.println("[CKTEST] ViewDocument:checkMetaStatus");
        boolean retVal = false;
        Database db = dbmgr.getDatabase();

        String deleteMessage = "Delete flag is not found";
        String expireMessage = "Expiration flag is not found";

        Where query = QueryBuilder
                .select(SelectResult.expression(Meta.id), SelectResult.expression(Meta.expiration),
                        SelectResult.expression(Meta.deleted))
                .from(DataSource.database(db))
                .where(Meta.id.equalTo(Expression.string(docId)));

        Where deleteQuery = QueryBuilder
                .select(SelectResult.expression(Meta.id), SelectResult.expression(Meta.expiration),
                        SelectResult.expression(Meta.deleted))
                .from(DataSource.database(db))
                .where(Meta.id.equalTo(Expression.string(docId)).and(Meta.deleted));

        try {
            ResultSet rs = query.execute();

            ArrayList<Result> rsItr = (ArrayList<Result>) rs.allResults();
            if (rsItr.isEmpty()) {
                rs = deleteQuery.execute();
                rsItr = (ArrayList<Result>) rs.allResults();
            }

            for (Result result : rsItr) {
                HashMap<String,Object> resultValues = (HashMap<String,Object>) result.toMap();

                for (String key : resultValues.keySet()) {
                    System.out.println(String.format("%s --> %s", key, resultValues.get(key)));
                }

                if (result.contains("deleted")) {
                    deleteMessage = "Delete flag found: " + result.getInt("deleted");
                }

                if (result.contains("expiration")) {
                    expireMessage = "Expiration flag found: " + Instant.ofEpochMilli(result.getLong("expiration"));
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        TextView dtv = findViewById(R.id.txt_deleted);
        dtv.setText(deleteMessage);

        TextView etv = findViewById(R.id.txt_expiry);
        etv.setText(expireMessage);

    }

    public void deleteDocument(View view) {
        System.out.println("[CKTEST] - ViewDocument:deleteDocument");
        Database db = dbmgr.getDatabase();

        try {
            Document doc = db.getDocument(docId);
            if (doc != null) {
                db.delete(doc);
                checkMetaStatus(docId);
                Toast toast = Toast.makeText(this, String.format(deleted,docId), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        checkMetaStatus(docId);
    }

    public void purgeDocument(View view) {
        System.out.println("[CKTEST] - ViewDocument:purgeDocument");
        Database db = dbmgr.getDatabase();

        try {
            db.purge(docId);
            Toast toast = Toast.makeText(this, String.format(purged,docId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        checkMetaStatus(docId);
    }

    public void setExpiry(View view) {
        Long ttlOffset = 0L;
        EditText expiry = findViewById(R.id.editTextExpiry);
        try {
            ttlOffset = Long.parseLong(expiry.getText().toString());
        } catch (NumberFormatException ne) {
            Toast toast = Toast.makeText(this, "Please enter valid offset in seconds", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }

        Database db = dbmgr.getDatabase();
        Instant ttl = Instant.now().plus(ttlOffset, ChronoUnit.SECONDS);
        try {
            db.setDocumentExpiration(docId, new Date(ttl.toEpochMilli()));
            Toast toast = Toast.makeText(this, String.format(expired,docId), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        checkMetaStatus(docId);
    }

}
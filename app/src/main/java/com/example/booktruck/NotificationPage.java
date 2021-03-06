/*
 *
 *
 *
 */
package com.example.booktruck;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktruck.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationPage extends AppCompatActivity {

    FirebaseFirestore db;
    static CollectionReference userRef;
    static CollectionReference bookRef;
    DocumentReference userDoc;

    private ArrayList<String> bookISBN = new ArrayList<>();
    private ArrayList<String> bookArray = new ArrayList<>();
    private ArrayList<String> requestArray = new ArrayList<>();
    private  ArrayAdapter<String> arrayAdapter;
    private ListView notifyListView;

    public String getCurrentUsername() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = "";
        String[] array = email.split("@");
        for (int i=0; i<array.length-1; i++) {
            username += array[i];
        }
        return username;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_page);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");
        userDoc = userRef.document(getCurrentUsername());

        notifyListView = findViewById(R.id.notify_list);
        final ArrayList<String> requestedList = new ArrayList<>();
        final DocumentReference userDoc = userRef.document(getCurrentUsername());

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //find books by corresponding ISBN
                    if (document.exists() && document.getData().containsKey("owned")) {
                        for (String ISBN : (ArrayList<String>) document.getData().get("owned")){
                            DocumentReference bookRef = db.collection("Books").document(ISBN);
                            bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists() && document.getData().containsKey("requests")) {
                                            Log.d("GET_BOOK_BY_ISBN", "DocumentSnapshot data: " + document.getData().get("title").toString());
                                            Map<String, Object> data = document.getData();
                                            //requestArray.add(data.get("request").toString());
                                            bookArray.add("Requested:   "+data.get("title").toString());
                                            bookISBN.add(ISBN);
                                            showRequestInDetail();
                                        } else {
                                            Log.d("GET_BOOK_BY_ISBN", "No such document");
                                        }
                                    } else {
                                        Log.d("GET_BOOK_BY_ISBN", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    protected void showRequestInDetail() {
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.content, bookArray);
        notifyListView.setAdapter(arrayAdapter);
        notifyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent requestDetail = new Intent(NotificationPage.this, showRequestInDetail.class);
                //requestDetail.putExtra("ListOfPeopleWhoRequest",requestArray);
                //requestDetail.putExtra("ParentClass", "ViewBook");
                requestDetail.putExtra("ISBN", bookISBN.get(position));
                startActivity(requestDetail);
            }
        });
    }

}

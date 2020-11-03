package com.example.booktruck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class RequestMenu extends AppCompatActivity {

    ListView bookList;
    ArrayAdapter<String> bookAdapter;
    ArrayList<String> dataList1;

    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_menu);

        bookList = findViewById(R.id.bookList_name);
        String[] books = {"book1          State", "book2          State", "book3          State", "book4          State", "book5          State"};
//        String[] books
        dataList1 = new ArrayList<>();
        dataList1.addAll(Arrays.asList(books));
        bookAdapter = new ArrayAdapter<>(this, R.layout.fake_list_content, dataList1);
        bookList.setAdapter(bookAdapter);

        btn1 = (Button) findViewById(R.id.find_book);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RequestMenu.this, SearchPage.class);
                startActivity(intent);
            }
        });

    }
}
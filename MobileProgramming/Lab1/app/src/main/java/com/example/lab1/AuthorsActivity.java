package com.example.lab1;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AuthorsActivity extends AppCompatActivity {
    ListView lvAuthors;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);

        lvAuthors = findViewById(R.id.lvAuthors);
        btnBack = findViewById(R.id.btnBack);

        ArrayList<String> authors = new ArrayList<>();
        authors.add("Вадим Глинский ИП-212");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_author, R.id.tvAuthorName, authors);
        lvAuthors.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }
}
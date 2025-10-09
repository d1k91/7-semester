package com.example.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1.data.AppDatabase;
import com.example.lab1.data.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionActivity extends AppCompatActivity {
    ListView lvUsers;
    Button btnNewUser, btnBack;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        lvUsers = findViewById(R.id.lvUsers);
        btnNewUser = findViewById(R.id.btnNewUser);
        btnBack = findViewById(R.id.btnBack);

        db = AppDatabase.getDatabase(this);

        loadUsers();

        btnNewUser.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        btnBack.setOnClickListener(v -> finish());

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            UserEntity selectedUser = (UserEntity) parent.getItemAtPosition(position);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putInt("current_user_id", selectedUser.id).apply();
            startActivity(new Intent(this, GameActivity.class));
        });

        lvUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            UserEntity selectedUser = (UserEntity) parent.getItemAtPosition(position);
            new AlertDialog.Builder(this)
                    .setTitle("Действия с пользователем")
                    .setMessage("Выберите действие для пользователя " + selectedUser.fullName)
                    .setPositiveButton("Редактировать", (dialog, which) -> {
                        Intent intent = new Intent(UserSelectionActivity.this, EditUserActivity.class);
                        intent.putExtra("user_id", selectedUser.id);
                        startActivity(intent);
                    })
                    .setNegativeButton("Удалить", (dialog, which) -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Удаление пользователя")
                                .setMessage("Вы уверены, что хотите удалить пользователя " + selectedUser.fullName + "?")
                                .setPositiveButton("Да", (dialog2, which2) -> {
                                    db.appDao().deleteUser(selectedUser);
                                    Toast.makeText(this, "Пользователь удален", Toast.LENGTH_SHORT).show();
                                    loadUsers();
                                })
                                .setNegativeButton("Нет", null)
                                .show();
                    })
                    .setNeutralButton("Отмена", null)
                    .show();
            return true;
        });
    }

    private void loadUsers() {
        List<UserEntity> users = db.appDao().getAllUsers();
        if (users.isEmpty()) {
            Toast.makeText(this, "Нет зарегистрированных пользователей. Создайте нового.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<UserEntity> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        lvUsers.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
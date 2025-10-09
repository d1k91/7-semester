package com.example.lab1.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface AppDao {
    @Insert
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserById(int userId);

    @Query("SELECT COUNT(*) FROM users WHERE fullName = :fullName")
    int countUsersByFullName(String fullName);

    @Insert
    void insertScore(ScoreEntity score);

    @Query("SELECT * FROM scores ORDER BY score DESC")
    List<ScoreEntity> getAllScores();

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);
}
package com.makhalibagas.aplikasinote.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.makhalibagas.aplikasinote.entities.Note;

import java.util.List;

/**
 * Created by Bagas Makhali on 6/18/2020.
 */
@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNote();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Delete
    void delete(Note note);


}

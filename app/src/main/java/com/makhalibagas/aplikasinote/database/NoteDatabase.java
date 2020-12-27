package com.makhalibagas.aplikasinote.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.makhalibagas.aplikasinote.dao.NoteDao;
import com.makhalibagas.aplikasinote.entities.Note;

/**
 * Created by Bagas Makhali on 6/18/2020.
 */

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {


    private static NoteDatabase noteDatabase;

    public static synchronized NoteDatabase getInstance(Context context){
        if (noteDatabase == null){
            noteDatabase = Room.databaseBuilder(context, NoteDatabase.class, "notedb").build();
        }

        return noteDatabase;
    }

    public abstract NoteDao noteDao();
}

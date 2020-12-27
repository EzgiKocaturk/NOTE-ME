package com.makhalibagas.aplikasinote.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makhalibagas.aplikasinote.R;
import com.makhalibagas.aplikasinote.adapter.NoteAdapter;
import com.makhalibagas.aplikasinote.database.NoteDatabase;
import com.makhalibagas.aplikasinote.entities.Note;
import com.makhalibagas.aplikasinote.ui.activity.AboutActivity;
import com.makhalibagas.aplikasinote.ui.activity.CreateNoteActivity;
import com.makhalibagas.aplikasinote.utils.onClickItem;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment implements onClickItem {

    private RecyclerView recyclerView;
    private final List<Note> noteList = new ArrayList<>();
    private NoteAdapter noteAdapter;
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_UPDATE = 2;
    private static final int REQUEST_SHOW = 3;
    private int onClickPosition = -1;
    public NotesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButtonCreateNote);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), CreateNoteActivity.class), REQUEST_ADD);
            }
        });
        recyclerView = view.findViewById(R.id.rv);
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);
        modeGrid();
        getNote(REQUEST_SHOW, false);
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.listView:
                modeList();
                break;
            case R.id.gridView:
                modeGrid();
                break;
            case R.id.aboutView:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void modeGrid() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }
    private void modeList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void getNote(final int requestCode, final boolean deleteNote){


        @SuppressLint("StaticFieldLeak")
        class GetNoteAsyncTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getInstance(getContext()).noteDao().getAllNote();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_SHOW){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else if (requestCode == REQUEST_ADD){
                    noteList.add(0 , notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                }else if (requestCode == REQUEST_UPDATE){
                    noteList.remove(onClickPosition);
                    if (deleteNote){
                        noteAdapter.notifyItemRemoved(onClickPosition);
                    }else {
                        noteList.add(onClickPosition, notes.get(onClickPosition));
                        noteAdapter.notifyItemChanged(onClickPosition);
                    }
                }
            }
        }

        new GetNoteAsyncTask().execute();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD && resultCode == RESULT_OK ){
            getNote(REQUEST_ADD, false);
        }else if (requestCode == REQUEST_UPDATE && resultCode == RESULT_OK){
            if (data != null){
                getNote(REQUEST_UPDATE, data.getBooleanExtra("NoteDelete", false));
            }
        }
    }
    @Override
    public void onClick(Note note, int position) {
        onClickPosition = position;
        Intent intent = new Intent(getContext(), CreateNoteActivity.class);
        intent.putExtra("EXTRA", true);
        intent.putExtra("EXTRA_NOTE", note);
        startActivityForResult(intent, REQUEST_UPDATE);
    }
}

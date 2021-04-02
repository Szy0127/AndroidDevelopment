package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;



import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("代办事项列表");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        //File dbFile = new File("/data/data/com.byted.camp.todolist/databases/todo.db");
        //dbFile.delete();

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoNote.TABLE_NAME, null,
                    null, null,
                    null, null,
                    TodoNote.COLUMN_PRIORITY + " DESC");

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoNote.COLUMN_CONTENT));

                int year = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_YEAR));
                int month = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_MONTH));
                int day = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_DAY));

                int intState = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoNote.COLUMN_PRIORITY));

                Note note = new Note(id);
                note.setContent(content);
                note.setYear(year);
                note.setMonth(month);
                note.setDay(day);
                note.setState(State.from(intState));
                note.setPriority(Priority.from(intPriority));

                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sort(result);
    }

    private void deleteNote(Note note) {
        if (database == null) {
            return;
        }
        int rows = database.delete(TodoNote.TABLE_NAME,
                TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void updateNode(Note note) {
        if (database == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(TodoNote.COLUMN_STATE, note.getState().intValue);

        int rows = database.update(TodoNote.TABLE_NAME, values,
                TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private boolean later(Note a,Note b)//return a is later than b
    {
        if(a.getYear()>b.getYear())return true;
        if(a.getYear()<b.getYear())return false;
        if(a.getMonth()>b.getMonth())return true;
        if(a.getMonth()<b.getMonth())return false;
        if(a.getDay()>b.getDay())return true;
        return false;
    }
    private List<Note> sort(List<Note> notes)
    {

        for(int i = 0;i < notes.size()-1;i++)
        {
            for(int j = 0;j < notes.size()-i-1;j++)
            {
                if(later(notes.get(j),notes.get(j+1)))
                {
                    Note tmp = notes.get(j);
                    notes.set(j,notes.get(j+1));
                    notes.set(j+1,tmp);
                }
            }
        }
        return notes;
    }

}

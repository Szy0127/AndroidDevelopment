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
import com.byted.camp.todolist.ui.NoteListAdapter;



import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD = 1002;
    public static final int REQUEST_CODE_MODIFY = 1003;
    public static final int REQUEST_CODE_CHANGE_COLOR = 1004;
    public static final int REQUEST_CODE_CHANGE_BACKGROUND = 1005;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private SharedPreferences spdata;
    SharedPreferences.Editor editor;

    private List<Note> notes;
    private int color_high;
    private int color_medium;
    private int color_low;
    public static String background;
    public int backgroundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("代办事项列表");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        spdata = getSharedPreferences("data",MODE_PRIVATE);
        editor = spdata.edit();
//        editor.remove("background");
//        editor.apply();
        color_high = spdata.getInt("color_high",0);
        color_medium = spdata.getInt("color_medium",0);
        color_low = spdata.getInt("color_low",0);
        if(color_high == 0 || color_medium == 0 || color_low == 0)
        {
            editor.putInt("color_high",Priority.High.color);
            editor.putInt("color_medium",Priority.Medium.color);
            editor.putInt("color_low",Priority.Low.color);
            editor.apply();
        }
        else {
                Priority.High.color = color_high;
                Priority.Medium.color = color_medium;
                Priority.Low.color = color_low;
            }
        background = spdata.getString("background","none");
        if(background.equals("none"))
        {
            editor.putString("background","jienigui");
            editor.apply();
            background = "jienigui";
        }
        try {
            backgroundID = R.drawable.class.getField(background).getInt(new R.drawable());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        getWindow().getDecorView().setBackgroundResource(backgroundID);

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

        notes = loadNotesFromDatabase();
        notesAdapter.refresh(notes);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_change_color:
                startActivityForResult(new Intent(this, ColorActivity.class),REQUEST_CODE_CHANGE_COLOR);
                return true;
            case R.id.action_change_background:
                startActivityForResult(new Intent(this, BackgroundActivity.class),REQUEST_CODE_CHANGE_BACKGROUND);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_ADD||requestCode == REQUEST_CODE_MODIFY)&& resultCode == Activity.RESULT_OK)
        {
            notes = loadNotesFromDatabase();
            notesAdapter.refresh(notes);
        }
        if(requestCode == REQUEST_CODE_CHANGE_COLOR&& resultCode == Activity.RESULT_OK)
        {
            notesAdapter.refresh(notes);
            if(Priority.High.color != color_high || Priority.Medium.color != color_medium || Priority.Low.color != color_low)
            {
                editor.putInt("color_high",Priority.High.color);
                editor.putInt("color_medium",Priority.Medium.color);
                editor.putInt("color_low",Priority.Low.color);
                editor.apply();
            }
        }
        if(requestCode == REQUEST_CODE_CHANGE_BACKGROUND&& resultCode == Activity.RESULT_OK)
        {
            try {
                backgroundID = R.drawable.class.getField(background).getInt(new R.drawable());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            getWindow().getDecorView().setBackgroundResource(backgroundID);

            editor.putString("background",background);
            editor.apply();
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

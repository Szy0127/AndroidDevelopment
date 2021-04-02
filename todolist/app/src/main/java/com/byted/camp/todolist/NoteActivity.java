package com.byted.camp.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.TodoNote;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.util.Calendar;


public class NoteActivity extends AppCompatActivity {


    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton lowRadio;
    private AppCompatRadioButton mediumRadio;
    private AppCompatRadioButton highRadio;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    int year,month,day;
    DatePicker datePicker;
    Button selectDate;
    TextView date_text;
    boolean showDate;
    boolean change;
    Note note;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDate = false;
        change = false;
        setContentView(R.layout.activity_note);

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadio = findViewById(R.id.btn_low);
        mediumRadio = findViewById(R.id.btn_medium);
        highRadio = findViewById(R.id.btn_high);
        try
        {
            note = (Note) getIntent().getSerializableExtra("note");

            String content = note.getContent();
            Priority priority = note.getPriority();

            change = true;
            title = "修改事项";
            year = note.getYear();
            month = note.getMonth();
            day = note.getDay();

            editText.setText(content);
            switch (priority) {
                case High:
                    highRadio.setChecked(true);
                    break;
                case Medium:
                    mediumRadio.setChecked(true);
                    break;
                case Low:
                    lowRadio.setChecked(true);
                    break;
            }
        }
        catch(Exception e) {
                mediumRadio.setChecked(true);
                Calendar calendar=Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month=calendar.get(calendar.MONTH)+1;
                day=calendar.get(calendar.DAY_OF_MONTH);
                title = "新建事项";
            }

        setTitle(title);
        editText.setFocusable(true);
        editText.requestFocus();

        date_text = (TextView)findViewById(R.id.date_text);
        selectDate = (Button)findViewById(R.id.select_date);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager m = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m .hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if(showDate)
                {
                    datePicker.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                    selectDate.setText("选择日期");
                }
                else{
                    datePicker.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    selectDate.setText("确认");
                }
                showDate = !showDate;
            }
        });

        String str=year+"年"+month+"月"+day+"日";
        date_text.setText(str);
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.init(year,month-1,day,new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker arg0,int yearr,int monthh,int dayy)
            {
                year=yearr;
                month=monthh+1;
                day=dayy;
                String str=year+"年"+month+"月"+day+"日";
                date_text.setText(str);
            }
        });


        addBtn = findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit()
    {
        CharSequence content = editText.getText();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(NoteActivity.this,
                    "代办事项为空！请输入！", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean succeed = saveNote2Database(content.toString().trim(),
                getSelectedPriority());
        if (succeed) {
            Toast.makeText(NoteActivity.this,
                    "提交成功！", Toast.LENGTH_SHORT).show();
            if(change)database.delete(TodoNote.TABLE_NAME,
                    TodoNote._ID + "=?",
                    new String[]{String.valueOf(note.id)});
            setResult(Activity.RESULT_OK);
        } else {
            Toast.makeText(NoteActivity.this,
                    "错误！", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    private boolean saveNote2Database(String content, Priority priority) {
        if (database == null || TextUtils.isEmpty(content)) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TodoNote.COLUMN_CONTENT, content);
        values.put(TodoNote.COLUMN_YEAR, year);
        values.put(TodoNote.COLUMN_MONTH, month);
        values.put(TodoNote.COLUMN_DAY, day);
        values.put(TodoNote.COLUMN_STATE, State.TODO.intValue);
        values.put(TodoNote.COLUMN_PRIORITY, priority.intValue);
        long rowId = database.insert(TodoNote.TABLE_NAME, null, values);
        return rowId != -1;
    }

    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.High;
            case R.id.btn_medium:
                return Priority.Medium;
            default:
                return Priority.Low;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (change && !TextUtils.isEmpty(editText.getText()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
            builder.setMessage("是否保存后再退出？");
            builder.setPositiveButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // 取消对话框
                    finish();
                }
            });

            builder.setNegativeButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss(); // 取消对话框
                    submit();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show(); // 显示对话框
        }
        else{
            finish();
        }
        return true;
    }

}

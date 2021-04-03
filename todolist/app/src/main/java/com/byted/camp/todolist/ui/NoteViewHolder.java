package com.byted.camp.todolist.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.byted.camp.todolist.MainActivity;
import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private final NoteOperator operator;

    private CheckBox checkBox;
    private TextView contentText;
    private TextView dateText;
    private TextView dayText;
    private View deleteBtn;
    Typeface typeface;
    public NoteViewHolder(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;

        checkBox = itemView.findViewById(R.id.checkbox);
        contentText = itemView.findViewById(R.id.text_content);
        dateText = itemView.findViewById(R.id.text_date);
        deleteBtn = itemView.findViewById(R.id.btn_delete);
        dayText = itemView.findViewById(R.id.days_remain);
        typeface =Typeface.createFromAsset(itemView.getContext().getAssets(),"STKAITI.TTF");
    }

    public void bind(final Note note) {
        contentText.setText(note.getContent());
        contentText.setTypeface(typeface);
        String str=note.getYear()+"-"+note.getMonth()+"-"+note.getDay();
        dateText.setText(str);

        Calendar date_now =Calendar.getInstance();
        Calendar deadline =Calendar.getInstance();
        date_now.set(date_now.get(Calendar.YEAR),date_now.get(Calendar.MONTH)+1,date_now.get(Calendar.DAY_OF_MONTH));
        deadline.set(note.getYear(),note.getMonth(),note.getDay());

        int remain_day = deadline.get(Calendar.DAY_OF_YEAR)-date_now.get((Calendar.DAY_OF_YEAR))+365*(deadline.get(Calendar.YEAR)-date_now.get(Calendar.YEAR));
        dayText.setText(String.valueOf(remain_day));

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(note.getState() == State.DONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setState(isChecked ? State.DONE : State.TODO);
                operator.updateNote(note);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.deleteNote(note);
            }
        });

        if (note.getState() == State.DONE) {
            contentText.setTextColor(Color.GRAY);
            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            contentText.setTextColor(Color.BLACK);
            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        itemView.setBackgroundColor(note.getPriority().color);
        itemView.getBackground().setAlpha(180);
    }
}

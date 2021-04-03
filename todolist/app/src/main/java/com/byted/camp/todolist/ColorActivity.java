package com.byted.camp.todolist;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.byted.camp.todolist.beans.Priority;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerPopup;
import top.defaults.colorpicker.ColorPickerView;

public class ColorActivity extends AppCompatActivity {
    TextView show_colorText;
    TextView color_highText;
    TextView color_mediumText;
    TextView color_lowText;
    Button submit;

    int color_high;
    int color_medium;
    int color_low;

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        setTitle("设置颜色");

        color_high = Priority.High.color;
        color_medium = Priority.Medium.color;
        color_low = Priority.Low.color;

        show_colorText = (TextView)findViewById(R.id.show_color);
        color_highText = (TextView)findViewById(R.id.color_high);
        color_mediumText = (TextView)findViewById(R.id.color_medium);
        color_lowText = (TextView)findViewById(R.id.color_low);

        color_highText.setBackgroundColor(Priority.High.color);
        color_mediumText.setBackgroundColor(Priority.Medium.color);
        color_lowText.setBackgroundColor(Priority.Low.color);
        radioGroup = findViewById(R.id.radio_group);
        submit = (Button)findViewById(R.id.btn_add);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Priority.High.color = color_high;
                Priority.Medium.color = color_medium;
                Priority.Low.color = color_low;
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.colorPicker);
        colorPickerView.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                show_colorText.setBackgroundColor(color);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.btn_high:
                        color_highText.setBackgroundColor(color);
                        color_high = color;
                        break;
                    case R.id.btn_medium:
                        color_mediumText.setBackgroundColor(color);
                        color_medium = color;
                        break;
                    case R.id.btn_low:
                        color_lowText.setBackgroundColor(color);
                        color_low = color;
                        break;
                }
            }
        });
    }


}
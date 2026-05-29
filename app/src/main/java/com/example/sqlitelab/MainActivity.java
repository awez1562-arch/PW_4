package com.example.sqlitelab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLHelper dbHelper;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SQLHelper(this);
        container = findViewById(R.id.container);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnShow = findViewById(R.id.btnShow);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Для примера добавляем тестовые данные
                long id = dbHelper.addStudent("Иванов Иван", 20);
                if (id != -1) {
                    Toast.makeText(MainActivity.this, "Студент добавлен с ID: " + id, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAllStudents();
            }
        });
    }

    private void displayAllStudents() {
        ArrayList<Person> students = dbHelper.getAllStudents();
        container.removeAllViews(); // Очищаем контейнер перед обновлением

        if (students.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Список студентов пуст");
            container.addView(emptyView);
            return;
        }

        for (Person student : students) {
            TextView textView = new TextView(this);
            textView.setText(student.getId() + ": " + student.getName() + ", возраст " + student.getAge());
            textView.setTextSize(16);
            textView.setPadding(8, 8, 8, 8);
            container.addView(textView);
        }
    }
}
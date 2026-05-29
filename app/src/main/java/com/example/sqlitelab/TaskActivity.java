package com.example.sqlitelab;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    private SQLHelper2 dbHelper;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        dbHelper = new SQLHelper2(this);
        container = findViewById(R.id.container);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnShow = findViewById(R.id.btnShow);

        // Добавление заказа
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddOrderDialog();
            }
        });

        // Показ всех заказов
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAllOrders();
            }
        });
    }

    // Диалог добавления заказа
    private void showAddOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новый заказ");

        // Создаём поля ввода
        final EditText inputClient = new EditText(this);
        inputClient.setHint("ФИО клиента");

        final EditText inputDate = new EditText(this);
        inputDate.setHint("Дата приёма (ДД.ММ.ГГГГ)");

        final EditText inputCost = new EditText(this);
        inputCost.setHint("Стоимость (руб.)");
        inputCost.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL |
                android.text.InputType.TYPE_CLASS_NUMBER);

        final EditText inputStatus = new EditText(this);
        inputStatus.setHint("Статус (Принят/В работе/Готов)");

        // Создаём контейнер для полей
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.addView(inputClient);
        layout.addView(inputDate);
        layout.addView(inputCost);
        layout.addView(inputStatus);

        builder.setView(layout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String client = inputClient.getText().toString().trim();
            String date = inputDate.getText().toString().trim();
            String costStr = inputCost.getText().toString().trim();
            String status = inputStatus.getText().toString().trim();

            if (client.isEmpty() || date.isEmpty() || costStr.isEmpty() || status.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            double cost;
            try {
                cost = Double.parseDouble(costStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некорректная стоимость!", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelper.addOrder(client, date, cost, status);
            if (id != -1) {
                Toast.makeText(this, "Заказ #" + id + " добавлен", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Отображение всех заказов
    private void displayAllOrders() {
        ArrayList<Order> orders = dbHelper.getAllOrders();
        container.removeAllViews();

        if (orders.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Список заказов пуст");
            emptyView.setTextSize(16);
            emptyView.setPadding(8, 16, 8, 16);
            container.addView(emptyView);
            return;
        }

        for (final Order order : orders) {
            // Создаём TextView для отображения заказа
            TextView textView = new TextView(this);
            textView.setText(order.toString());
            textView.setTextSize(14);
            textView.setPadding(8, 12, 8, 12);
            textView.setBackgroundColor(0xFFF5F5F5);

            // Долгое нажатие для удаления
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(TaskActivity.this)
                            .setTitle("Удалить заказ")
                            .setMessage("Удалить заказ #" + order.getId() + "?")
                            .setPositiveButton("Удалить", (dialog, which) -> {
                                dbHelper.deleteOrder(order.getId());
                                displayAllOrders();
                                Toast.makeText(TaskActivity.this,
                                        "Заказ #" + order.getId() + " удалён",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                    return true;
                }
            });

            // Клик для редактирования
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditOrderDialog(order);
                }
            });

            container.addView(textView);
        }
    }

    // Диалог редактирования заказа
    private void showEditOrderDialog(final Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать заказ #" + order.getId());

        final EditText inputClient = new EditText(this);
        inputClient.setText(order.getClientName());
        inputClient.setHint("ФИО клиента");

        final EditText inputDate = new EditText(this);
        inputDate.setText(order.getOrderDate());
        inputDate.setHint("Дата приёма");

        final EditText inputCost = new EditText(this);
        inputCost.setText(String.valueOf(order.getCost()));
        inputCost.setHint("Стоимость");
        inputCost.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL |
                android.text.InputType.TYPE_CLASS_NUMBER);

        final EditText inputStatus = new EditText(this);
        inputStatus.setText(order.getStatus());
        inputStatus.setHint("Статус");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        layout.addView(inputClient);
        layout.addView(inputDate);
        layout.addView(inputCost);
        layout.addView(inputStatus);

        builder.setView(layout);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            order.setClientName(inputClient.getText().toString().trim());
            order.setOrderDate(inputDate.getText().toString().trim());
            order.setCost(Double.parseDouble(inputCost.getText().toString().trim()));
            order.setStatus(inputStatus.getText().toString().trim());

            dbHelper.updateOrder(order);
            displayAllOrders();
            Toast.makeText(TaskActivity.this, "Заказ обновлён", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}
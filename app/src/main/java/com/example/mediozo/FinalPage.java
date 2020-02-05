package com.example.mediozo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinalPage extends AppCompatActivity {
    private TextView msgText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_page);
        Toast.makeText(this, "Your Appointment is Scheduled for Tommorow!", Toast.LENGTH_SHORT).show();
        String message = getIntent().getExtras().getString("details");
        msgText = findViewById(R.id.messageDone);
        msgText.setText(message);
        Button btn = findViewById(R.id.finish);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}

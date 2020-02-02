package com.example.mediozo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConfirmActivity extends AppCompatActivity {

    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private  String concattobedisable="";
    private TextView message;
    private String messagetoshow;
    Button fixAppointment;
    private String doctorname;
    private String deptname;
    private TextView details;
    private  String nameofuser;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        details = findViewById(R.id.details);
        btn1 = findViewById(R.id.ts1);
        btn2 = findViewById(R.id.ts2);
        btn3 = findViewById(R.id.ts3);
        btn4 = findViewById(R.id.ts4);
        btn5 = findViewById(R.id.ts5);
        message = findViewById(R.id.message);

        getSupportActionBar().setTitle("Confirm");
        doctorname = getIntent().getExtras().getString("doctorname");
        deptname = getIntent().getExtras().getString("deptname");
        nameofuser = getIntent().getExtras().getString("nameofuser");
        phoneNumber = getIntent().getExtras().getString("phonenumber");
        fixAppointment = findViewById(R.id.finalBtn);

        details.setText("Name : " + nameofuser + "\n" + "Phone Number : " + phoneNumber
                + "\n" + "Department: " + deptname + "\n" + "Appointment with " + doctorname );

        setTimeSlots(deptname,doctorname);
    }

    public void setTimeSlots(final String deptname , final String doctorname){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Time Slot");
        final ArrayList<String> listTime = new ArrayList<>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot sub : dataSnapshot.getChildren()){
                    String time = sub.getValue().toString();
                    listTime.add(time);
                }
                btn1.setText(listTime.get(0));
                btn2.setText(listTime.get(1));
                btn3.setText(listTime.get(2));
                btn4.setText(listTime.get(3));
                btn5.setText(listTime.get(4));
                disableUnavailableTimeSlot(deptname,doctorname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void disableUnavailableTimeSlot(String deptname , String doctorname){
        DatabaseReference ref = FirebaseDatabase
                .getInstance().getReference("Department").child(deptname).child(doctorname).child("Time Slot");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sub : dataSnapshot.getChildren()){
                    String position = sub.getKey();
                    String valueofposition = sub.getValue().toString();
                    if(valueofposition.equals("false")){
                        concattobedisable += position + " ";
                    }

                    if(concattobedisable.contains(btn1.getTag().toString())){
                        btn1.setEnabled(false);
                    }if(concattobedisable.contains(btn2.getTag().toString())){
                        btn2.setEnabled(false);
                    }if(concattobedisable.contains(btn3.getTag().toString())){
                        btn3.setEnabled(false);
                    }if (concattobedisable.contains(btn4.getTag().toString())){
                        btn4.setEnabled(false);
                    }if (concattobedisable.contains(btn5.getTag().toString())){
                        btn5.setEnabled(false);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void selectTime(View view){
        Button btn = findViewById(view.getId());
        final String tag = view.getTag().toString();
        messagetoshow = btn.getText().toString();
        btn.setBackgroundColor(Color.GREEN);
        btn.setTextColor(Color.WHITE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!btn1.getText().toString().equals(tag)){
                    btn1.setVisibility(View.INVISIBLE);
                }else{
                    btn1.setVisibility(View.VISIBLE);
                }if(!btn2.getText().toString().equals(tag)){
                    btn2.setVisibility(View.INVISIBLE);
                }if(!btn3.getText().toString().equals(tag)){
                    btn3.setVisibility(View.INVISIBLE);
                }if(!btn4.getText().toString().equals(tag)){
                    btn4.setVisibility(View.INVISIBLE);
                }if(!btn5.getText().toString().equals(tag)){
                    btn5.setVisibility(View.INVISIBLE);
                }
                message.setText("Time Slot Picked \n" + messagetoshow );
                message.setVisibility(View.VISIBLE);
                fixAppointment.setVisibility(View.VISIBLE);
                fixAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appointmentSuccess(tag);
                    }
                });
            }
        },1000);
    }

    public  void appointmentSuccess(final String position){
        FirebaseDatabase.getInstance().getReference("Department").child(deptname)
                .child(doctorname).child("Time Slot").child(position).setValue("false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Department").child(deptname)
                .child(doctorname).child("Time Slot");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0 ;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue().toString().equals("false")){
                        count++;
                    }
                }
                if(count == 5){
                    FirebaseDatabase.getInstance().getReference("Department")
                            .child(deptname).child(doctorname).child("status").setValue("false");
                }

                Intent finalpage = new Intent(getApplicationContext(),FinalPage.class);
                finalpage.putExtra("details" , "Name : " + nameofuser + "\n" + "Phone Number : " + phoneNumber
                        + "\n" + "Department: " + deptname + "\n" + "Appointment with " + doctorname +
                        "\n" + "TIME : " + messagetoshow );
                finalpage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(finalpage);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

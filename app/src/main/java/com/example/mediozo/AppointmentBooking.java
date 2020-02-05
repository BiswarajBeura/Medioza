package com.example.mediozo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AppointmentBooking extends AppCompatActivity {
    Spinner deptSpinner;
    EditText name ;
    String namedoc;
    EditText phonenumber;
    Button nextButton1;

    private ListView doclist;
    private TextView available;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        getSupportActionBar().setTitle("Appointment Booking");

        deptSpinner = findViewById(R.id.deptDropDown);
        name = findViewById(R.id.name);
        phonenumber = findViewById(R.id.phnnumber);
        nextButton1 = findViewById(R.id.next1);
        doclist = findViewById(R.id.doclist);
        available = findViewById(R.id.ava);

        String[] deptnames = new String[] {"ENT","Cardiology","Anatomy","General Physician","Neurologist","Orthopedic" , "Pediatrician"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(),R.layout.support_simple_spinner_dropdown_item, deptnames);
        deptSpinner.setPrompt("Select Department");
        deptSpinner.setAdapter(adapter);

        nextButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameValue = name.getText().toString();
                String phoneNumber = phonenumber.getText().toString();
                String dept = deptSpinner.getSelectedItem().toString();
                if(nameValue.isEmpty() || phoneNumber.isEmpty()){
                    Toast.makeText(AppointmentBooking.this, "Enter Your Details First!", Toast.LENGTH_SHORT).show();
                }else
                initAllDoctors(dept);
            }
        });


    }

    public void initAllDoctors(final String dept){
        final ArrayList<String> listofdoc =  new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Department").child(dept);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot sub : dataSnapshot.getChildren()) {
                    namedoc = sub.getKey();
                    String status = sub.child("status").getValue().toString();
                    if(status.equals("true")){
                        listofdoc.add(namedoc);
                    }
                }
                if(listofdoc.size()!= 0) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, listofdoc);
                    doclist.setAdapter(arrayAdapter);
                    available.setVisibility(View.VISIBLE);
                }else {
                    available.setVisibility(View.INVISIBLE);
                    doclist.setAdapter(null);
                    Toast.makeText(AppointmentBooking.this, "No Doctors Available!", Toast.LENGTH_SHORT).show();
                }

                doclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String doctorname = listofdoc.get(position);
                        String deptname = dept;
                        String nameofuser = name.getText().toString();
                        String phoneNumber = phonenumber.getText().toString();
                        Intent confirm = new Intent(getApplicationContext(),ConfirmActivity.class);
                        confirm.putExtra("doctorname" , doctorname);
                        confirm.putExtra("deptname" , deptname);
                        confirm.putExtra("nameofuser" , nameofuser);
                        confirm.putExtra("phonenumber" , phoneNumber);
                        confirm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(confirm);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

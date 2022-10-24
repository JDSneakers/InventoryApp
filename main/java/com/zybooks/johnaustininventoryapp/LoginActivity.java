package com.zybooks.johnaustininventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText user_login;
    private EditText password;
    private Button login;
    private Button create_user;

    private InventoryDatabase mInventoryDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_login = (EditText) findViewById(R.id.user_login);
        password = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.login_user);
        create_user = (Button) findViewById(R.id.create_user);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(user_login.getText().toString(), password.getText().toString());
            }
        });

        create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = user_login.getText().toString();
                String userPassword = password.getText().toString();

                if (username.equals("") || userPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, "No field can be left blank", Toast.LENGTH_LONG).show();
                }


                else {
                    if (mInventoryDb.checkUser(username)) {
                        Toast.makeText(LoginActivity.this, "This username already exists", Toast.LENGTH_LONG).show();
                    }
                    else {

                        boolean result = mInventoryDb.addUser(username, userPassword);

                        if (result) {
                            Toast.makeText(LoginActivity.this, "Login successfully added, you may now login", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Create user failed", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }
        });
    }

    private void validate (String username, String password) {
        boolean result = mInventoryDb.grantUserLogin(username, password);
        if (result) {
            Intent intent = new Intent(LoginActivity.this, CategoryActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
        }
    }
}
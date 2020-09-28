package ru.kolesnikovdmitry.blogapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import ru.kolesnikovdmitry.blogapp.R;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ACT_SIGN_IN = 10;
    public static final int REQUEST_CODE_ACT_SIGN_UP = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnAccounts = findViewById(R.id.btnAccountsActMain);

        btnAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ACT_SIGN_IN);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ACT_SIGN_UP);
                } catch (Throwable th) {
                    Toast.makeText(getApplicationContext(), th.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("myApp", Objects.requireNonNull(th.getMessage()));
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ACT_SIGN_IN);
                } catch (Throwable th) {
                    Toast.makeText(getApplicationContext(), th.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("myApp", Objects.requireNonNull(th.getMessage()));
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ACT_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, PostsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Not saved", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ACT_SIGN_UP:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, PostsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Not saved", Toast.LENGTH_LONG).show();
                }
        }
    }
}
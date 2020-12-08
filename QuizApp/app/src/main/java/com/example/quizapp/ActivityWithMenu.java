package com.example.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityWithMenu extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tests) {
            Intent intent = new Intent(ActivityWithMenu.this, TestActivity.class);
            startActivity(intent);
        } else if (id == R.id.scores) {
            Intent intent = new Intent(ActivityWithMenu.this, ScoreActivity.class);
            startActivity(intent);
        } else if (id == R.id.sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ActivityWithMenu.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

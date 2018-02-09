package com.smokinmonkey.vgangapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.smokinmonkey.vgangapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlogActivity extends AppCompatActivity {

    // debug tag
    private static final String TAG = "BlogActivity";

    // butter knife bind views
    @BindView(R.id.fab_add_new_post)
    FloatingActionButton mAddNewPostFab;

    @OnClick(R.id.fab_add_new_post)
    public void addNewPost() {
        Intent i = new Intent(getApplicationContext(), NewBlogActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        // bind butter knife with this activity
        ButterKnife.bind(this);
    }
}

package com.menghuidream.notebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    EditText editTextTitle, editTextContent;

    private String old_title = "";
    private String old_content = "";
    private String old_time = "";
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    private boolean tagChange = false;
    public Intent intent = new Intent();
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);

        toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑笔记");
        //设置返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage();
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode", 0);

        if(openMode == 3) {
            id = getIntent.getLongExtra("id", 0);
            old_title = getIntent.getStringExtra("title");
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            tag = getIntent.getIntExtra("tag", 1);
            editTextTitle.setText(old_title);
            editTextContent.setText(old_content);
            editTextContent.setSelection(old_content.length());//将光标移至文字末尾

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_BACK){
            autoSetMessage();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String dateToStr() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public void autoSetMessage() {
        if(openMode == 2) {
            if(editTextTitle.getText().toString().equals("") && editTextContent.getText().toString().equals("")) {
                intent.putExtra("mode", -1);
            }
            else {
                intent.putExtra("mode", 0);
                intent.putExtra("title", editTextTitle.getText().toString());
                intent.putExtra("content", editTextContent.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        }
        else if(openMode == 3) {
            if(editTextTitle.getText().toString().equals(old_title) && editTextContent.getText().toString().equals(old_content) && !tagChange) {
                intent.putExtra("mode", -1);
            }
            else {
                intent.putExtra("mode", 1);
                intent.putExtra("title", editTextTitle.getText().toString());
                intent.putExtra("content", editTextContent.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                new AlertDialog.Builder(EditActivity.this)
                        .setTitle("删除笔记")
                        .setMessage("确定要删除这条笔记吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (openMode == 2){
                                    intent.putExtra("mode", -1);
                                    setResult(RESULT_OK, intent);
                                }
                                else if (openMode == 3){
                                    intent.putExtra("mode", 4);//mode = 4 删除
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}

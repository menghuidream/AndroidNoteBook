package com.menghuidream.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private NoteDatabase db;
    private NoteAdapter adapter;
    private List<NoteBean> noteList = new ArrayList<>();

    String TAG = "tag";
    FloatingActionButton button;
    //TextView textViewTitle;
    private ListView listView;
    private Toolbar toolbar;

    //弹出菜单
    private PopupWindow popupWindow;
    private PopupWindow popupWindowCover;
    private ViewGroup customView;
    private ViewGroup coverView;
    private LayoutInflater inflater;
    private RelativeLayout mainLayout;
    private WindowManager windowManager;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.floatingActionButton);
        //textViewTitle = findViewById(R.id.textViewTitle);
        listView = findViewById(R.id.listView);
        toolbar = findViewById(R.id.toolbarMain);
        adapter = new NoteAdapter(getApplicationContext(), noteList);
        refreshListView();//刷新列表
        listView.setAdapter(adapter);
        //设置自定义的菜单栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("笔记本");
        initPopUpView();
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpView();
            }
        });

        listView.setOnItemClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode", 2); // 2表示新建
                startActivityForResult(intent, 0);
            }
        });

    }



    // 接收startActivityForResult的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int returnMode;
        long noteId;
        returnMode = data.getExtras().getInt("mode",-1);
        noteId = data.getExtras().getLong("id",0);

        super.onActivityResult(requestCode, resultCode, data);

        if(returnMode == 1)//修改笔记
        {
            String title = data.getExtras().getString("title");
            String time = data.getExtras().getString("time");
            String content = data.getExtras().getString("content");
            int tag = data.getExtras().getInt("tag", 1);
            NoteBean newNote = new NoteBean(title,content,time,tag);
            newNote.setId(noteId);
            DatabaseOperation databaseOperation = new DatabaseOperation(getApplicationContext());
            databaseOperation.open();
            databaseOperation.updateNoteBean(newNote);
            databaseOperation.close();
        }
        else if(returnMode == 0) //创建新的笔记
        {
            String title = data.getExtras().getString("title");
            String time = data.getExtras().getString("time");
            String content = data.getExtras().getString("content");
            int tag = data.getExtras().getInt("tag", 1);
            NoteBean noteBean = new NoteBean(title, content, time, 1);
            //textViewTitle.setText(edit);
            DatabaseOperation databaseOperation = new DatabaseOperation(getApplicationContext());
            databaseOperation.open();
            databaseOperation.addNoteBean(noteBean);
            databaseOperation.close();
        }
        else if(returnMode == 4) //删除笔记
        {
            NoteBean newNote = new NoteBean();
            newNote.setId(noteId);
            DatabaseOperation databaseOperation = new DatabaseOperation(getApplicationContext());
            databaseOperation.open();
            databaseOperation.removeNoteBean(newNote);
            databaseOperation.close();
        }

        refreshListView();//刷新列表
    }

    private void refreshListView() {
        DatabaseOperation databaseOperation = new DatabaseOperation(getApplicationContext());
        databaseOperation.open();
        if (noteList.size() > 0) {
            noteList.clear();
        }
        noteList.addAll(databaseOperation.getAllNoteBeans());
        databaseOperation.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listView:
                NoteBean currentNote = (NoteBean) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id", currentNote.getId());
                intent.putExtra("title", currentNote.getTitle());
                intent.putExtra("content", currentNote.getContent());
                intent.putExtra("time", currentNote.getTime());
                intent.putExtra("tag", currentNote.getTag());
                intent.putExtra("mode", 3); // 3表示编辑
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//加载菜单

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索笔记");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要删除所有笔记吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db = new NoteDatabase(MainActivity.this);
                                SQLiteDatabase sql = db.getWritableDatabase();
                                sql.execSQL("delete from notes");
                                sql.execSQL("update sqlite_sequence SET seq = 0 where name ='notes'");
                                sql.close();
                                db.close();
                                refreshListView();
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

    public void initPopUpView() {
        inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = (ViewGroup) inflater.inflate(R.layout.activity_setting, null);
        coverView = (ViewGroup) inflater.inflate(R.layout.activity_setting_cover, null);
        mainLayout = findViewById(R.id.mainLayout);
        windowManager = getWindowManager();
        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
    }

    public void showPopUpView() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        popupWindowCover = new PopupWindow(coverView, width, height, false);
        popupWindow = new PopupWindow(customView, (int)(width*0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        findViewById(R.id.mainLayout).post(new Runnable() {
            @Override
            public void run() {
                popupWindowCover.showAtLocation(mainLayout, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(mainLayout, Gravity.NO_GRAVITY, 0, 0);

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupWindowCover.dismiss();
                    }
                });
            }
        });
    }
}
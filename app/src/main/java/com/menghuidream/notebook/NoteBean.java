package com.menghuidream.notebook;

public class NoteBean {
    private long id;//id
    private String title;//标题
    private String content;//内容
    private String time;//时间
    private int tag;//标签

    public NoteBean() {
    }
    public NoteBean(String title, String content, String time, int tag) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return title + "\n" + time.substring(5,16) + " " + id;
    }
}

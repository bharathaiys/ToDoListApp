package com.teamnougat.todolistapp;

public class Item {

    String title, type, date;

    public Item(String title, String type, String date)
    {
        this.title = title;
        this.type = type;
        this.date = date;
    }
    public String getTitle()
    {
        return title;
    }
    public String getType()
    {
        return type;
    }
    public String getDate()
    {
        return date;
    }
}

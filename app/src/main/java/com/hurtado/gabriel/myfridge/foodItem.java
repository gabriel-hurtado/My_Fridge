package com.hurtado.gabriel.myfridge;

        import java.io.Serializable;

class foodItem implements Serializable {



    private long position;

    private String name = "";

    private String date;


    public foodItem(String name, String date) {
        this.setName(name);
        this.setDate(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    }


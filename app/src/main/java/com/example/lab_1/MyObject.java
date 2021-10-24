package com.example.lab_1;

public class MyObject {
    private int _id;
    private String name;
    private double number;
    private boolean logic;

    public MyObject(int id,String name,double number,boolean logic){
        this._id = id;
        this.name = name;
        this.number = number;
        this.logic = logic;
    }

    public MyObject(){}
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public boolean isLogic() {
        return logic;
    }

    public void setLogic(boolean logic) {
        this.logic = logic;
    }

    public String toString(){
        return "Название: " + getName() + "\nКоличество: " + getNumber() + "\nЛогика: " + isLogic();
    }
}

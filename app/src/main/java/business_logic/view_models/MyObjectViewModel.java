package business_logic.view_models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class MyObjectViewModel implements Parcelable {
    private int Id;
    private double Number;
    private String Name;
    private boolean Logic;

    public MyObjectViewModel(int Id, String name, double number, boolean logic) {
        this.Id = Id;
        this.Name = name;
        this.Number = number;
        this.Logic = logic;
    }

    public static final Creator<MyObjectViewModel> CREATOR = new Creator<MyObjectViewModel>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public MyObjectViewModel createFromParcel(Parcel in) {
            int Id = in.readInt();
            String Name = in.readString();
            double Number = in.readDouble();
            boolean Logic = in.readBoolean();

            return new MyObjectViewModel(Id, Name, Number, Logic);
        }

        @Override
        public MyObjectViewModel[] newArray(int size) {
            return new MyObjectViewModel[size];
        }
    };


    public MyObjectViewModel(){}
    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public double getNumber() {
        return Number;
    }

    public void setNumber(double number) {
        Number = number;
    }

    public boolean isLogic() {
        return Logic;
    }

    public void setLogic(boolean logic) {
        this.Logic = logic;
    }

    public String toString(){
        return "Название: " + getName() + "\nКоличество: " + getNumber() + "\nЛогика: " + isLogic();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeDouble(Number);
        parcel.writeString(Name);
        parcel.writeBoolean(Logic);
    }
}

package business_logic.binding_models;

public class MyObjectBindingModel {
    private Integer Id;
    private double Number;
    private String Name;
    private boolean Logic;

    public MyObjectBindingModel(Integer Id, String name, double number, boolean logic) {
        this.Id = Id;
        this.Name = name;
        this.Number = number;
        this.Logic = logic;
    }

    public MyObjectBindingModel() {
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public double getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isLogic() {
        return Logic;
    }

    public void setLogic(boolean logic) {
        Logic = logic;
    }
}

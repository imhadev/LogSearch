package sample;

public class TreeElement {
    private String name;
    private boolean check;

    public TreeElement(String name, boolean check) {
        this.name = name;
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public boolean getCheck() {
        return check;
    }

    @Override
    public String toString() {
        return "TreeElement{" +
                "name='" + name + '\'' +
                ", check=" + check +
                '}';
    }
}
package sample;

import java.util.ArrayList;

public class TreeElement {
    private String name;
    private boolean check;
    private ArrayList<Integer> patternMatches;

    public TreeElement(String name, boolean check, ArrayList<Integer> patternMatches) {
        this.name = name;
        this.check = check;
        this.patternMatches = patternMatches;
    }

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

    public ArrayList<Integer> getpatternMatches() {
        return patternMatches;
    }

    @Override
    public String toString() {
        return "TreeElement{" +
                "name=" + name + '\'' +
                ", check=" + check + '\'' +
                ", patternMatches=" + patternMatches +
                '}';
    }
}
package sample;

import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;

public class TextAreaTab {

    public InlineCssTextArea tabTextArea;
    public ArrayList<Integer> curElementsArray;
    public int curPosIndex;

    public TextAreaTab(InlineCssTextArea tabTextArea, ArrayList<Integer> curElementsArray, int curPosIndex) {
        this.tabTextArea = tabTextArea;
        this.curElementsArray = curElementsArray;
        this.curPosIndex = curPosIndex;
    }
}

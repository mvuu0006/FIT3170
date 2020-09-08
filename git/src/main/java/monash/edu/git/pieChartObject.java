package monash.edu.git;

import java.lang.reflect.Array;

public class pieChartObject {
//    datasets: [
//    {
//        label: 'Rainfall',
//                fill: false,
//            lineTension: 0.5,
//            backgroundColor: 'rgba(240,255,255,1)',
//            borderColor: 'rgba(240,255,255,1)',
//            borderWidth: 2,
//            data: [0,0,65, 59, 80,0,0, 81, 56]
//    },
//
//    {
//        label: 'Summer',
//                fill: false,
//            lineTension: 0.5,
//            backgroundColor: 'rgb(4,151,151)',
//            borderColor: 'rgb(17,194,194)',
//            borderWidth: 2,
//            data: [6, 79, 24, 55, 34, 89,111]
//    }
//            ],
    private String label;
    private String backgroundColor;
    private String borderColor;
    public int[] data;


    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}

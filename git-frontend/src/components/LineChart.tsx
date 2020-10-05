import React from 'react';
import './App.css';

import {Line} from 'react-chartjs-2';

class LineChart extends  React.Component<any> {
    public labels;
    public backgroundColor;
    public borderColor;
    public dataSet;
    public lineChartAttributes;
    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.data != null) {
                    if (this.props.data.length != 0) {
                        this.backgroundColor = this.props.data.backgroundColor;
                        this.borderColor = this.props.data.borderColor;
                        this.dataSet = this.props.data.data;
                        this.labels=this.props.data.labels;
                        this.getContributions();
                        return (
                            <div>
                                <Line
                                    data={this.state}
                                    options={{
                                        title: {
                                            display: true,
                                            text: 'Commits Timeline',
                                            fontSize: 20
                                        },
                                        legend: {
                                            display: true,
                                            position: 'right'
                                        }
                                    }}
                                />
                            </div>
                        );
                    }}
    return <div></div>}



    getContributions()
    {
        this.constructDataSet();
        this.state = {
            labels: ['Jan', 'Feb', 'March',
                'April', 'May', 'June', 'July', 'Aug','Sept', 'Oct', 'Nov', 'Dec'],
            datasets: this.lineChartAttributes,
        }
    }

    constructDataSet()
    {


        let length=this.labels.length;
        this.lineChartAttributes =[];
        for (let i=0;i<length;i++)
        {
                this.lineChartAttributes.push({
                        'label': this.labels[i],
                        'fill': true,
                        'lineTension': 0,
                        'backgroundColor': this.chooseColour(i),
                        'borderColor': this.chooseColour(i),
                        'data': this.dataSet[i]
                    }
                );
           // }
        }
    }

    chooseColour(index)
    {
        var colors=[
        "rgba(  255,165,0,0.5)","rgba(  0,255,127,0.5)","rgba(0,0,255,0.5)", "rgba(0,255,2550,0.5)", "rgba(127,255,212,0.5)",
        "rgba(240,255,255,0.5)", "rgba(  245,245,220,0.5)", "rgba(  255,228,196,0.5)",
        "rgba(  0,0,0,0.5)", "rgba(  255,235,205,0.5)", "rgba(  0,0,255,0.5)",
        "rgba(  138,43,226,0.5)", "rgba(  165,42,42,0.5)", "rgba(  222,184,135,0.5)",
        "rgba(  127,255,0,0.5)", "rgba(  210,105,30,0.5)",
        "rgba(  255,127,80,0.5)", "rgba(  100,149,237,0.5)", "rgba(  255,248,220,0.5)",
        "rgba(  220,20,60,0.5)", "rgba(  0,255,255,0.5)", "rgba(  0,0,139,0.5)",
        "rgba(  184,134,11,0.5)", "rgba(  169,169,169,0.5)",
        "rgba(  0,100,0,0.5)", "rgba(  169,169,169,0.5)", "rgba(  189,183,107,0.5)",
        "rgba(  139,0,139,0.5)", "rgba(  85,107,47,0.5)",
        ];
        return colors[index%29]
    }

}

export default LineChart;
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
                //this.props.data[0].backgroundColor[0]
                this.backgroundColor = this.props.data[0].backgroundColor;
                this.borderColor = this.props.data[0].borderColor;
                this.dataSet = this.props.data[0].data;
                this.labels=this.props.data[0].labels;
                this.getContributions();
                return (
                    <div>
                        <Line
                            data={this.state}
                            options={{
                                title: {
                                    display: true,
                                    text: 'Average Rainfall per month',
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
            }
        }
    return <div></div>
}



    getContributions()
    {
        this.constructDataSet();
        this.state = {
            labels: ['January', 'February', 'March',
                'April', 'May', 'June', 'July', 'August','September', 'October', 'November', 'December'],
            datasets: this.lineChartAttributes,
            //     [
            //     {
            //         label: 'Rainfall',
            //         fill: false,
            //         lineTension: 0.5,
            //         backgroundColor: 'rgba(240,255,255,1)',
            //         borderColor: 'rgba(240,255,255,1)',
            //         borderWidth: 2,
            //         data: [0,0,65, 59, 80,0,0, 81, 56]
            //     },
            //
            //     {
            //         label: 'Summer',
            //         fill: false,
            //         lineTension: 0.5,
            //         backgroundColor: 'rgb(4,151,151)',
            //         borderColor: 'rgb(17,194,194)',
            //         borderWidth: 2,
            //         data: [6, 79, 24, 55, 34, 89,111]
            //     }
            // ],
            // options:{
            //     responsive: true,
            //     scales: {
            //         xAxes: [{
            //             display: true,
            //             gridLines: {
            //                 display: true,
            //                 color: "#e82020"
            //             },
            //             scaleLabel: {
            //                 display: true,
            //                 labelString: 'Month'
            //             }
            //         }],
            //         yAxes: [{
            //             display: true,
            //             gridLines: {
            //                 display: true,
            //                 color: "#d91818"
            //             },
            //
            //         }]
            //     }
            // }
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
                    'fill':false,
                    'lineTension': 0,
                    'backgroundColor': this.backgroundColor[i],
                    'borderColor': this.borderColor[i],
                    'data' : this.dataSet[i]
                }
            );
        }
        console.log(this.lineChartAttributes);
    }

}

export default LineChart;
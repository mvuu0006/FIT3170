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
        if (this.props.data.state != null) {
            if (this.props.data.state.length != 0) {
                this.backgroundColor = this.props.data.state[0].backgroundColor;
                this.borderColor = this.props.data.state[0].borderColor;
                this.dataSet = this.props.data.state[0].data;
                this.labels=this.props.data.state[0].labels;
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
                        'backgroundColor': this.backgroundColor[i],
                        'borderColor': this.borderColor[i],
                        'data': this.dataSet[i]
                    }
                );
           // }
        }
    }

}

export default LineChart;
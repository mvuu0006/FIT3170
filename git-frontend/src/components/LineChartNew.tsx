import React from 'react';
import './App.css';

import {Line} from 'react-chartjs-2';

class LineChartNew extends  React.Component<any> {
    
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <Line
                    data={this.getLineData()}
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


    getLineData() {
        let line_data = {labels: ['Jan', 'Feb', 'March',
        'April', 'May', 'June', 'July', 'Aug','Sept', 'Oct', 'Nov', 'Dec']};
        let datasets = [];
        
    }

    chooseColour(index){
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
        return colors[index%29];
    }

    componentWillReceiveProps(nextProps) {
        this.setState({data: nextProps.data});
    }
}

export default LineChartNew;
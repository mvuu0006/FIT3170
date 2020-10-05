import React from 'react';
import './App.css';

import {Line} from 'react-chartjs-2';

class LineChart extends  React.Component<{data?: any}, {data?: any}> {
    
    constructor(props) {
        super(props);
        this.state = {data: props.data}
    }

    render() {

        if (this.state.data != null){
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
        else return <div></div>
    }


    getLineData() {
        // Get commits per month for each contributor
        let commit_timeline = {}
        for (let i = 0; i < this.state.data.length; i++) {
            if (commit_timeline[this.state.data[i]["author"]] === undefined) {
                commit_timeline[this.state.data[i]["author"]] = [0,0,0,0,0,0,0,0,0,0,0,0];
            }
            commit_timeline[this.state.data[i]["author"]][new Date(this.state.data[i]["date"]).getUTCMonth()-1]++;
            
        }
        // Add each line to a list
        let line_data: Object[] = [];
        let x = 0;
        for (var key of Object.keys(commit_timeline)) {
            let entry = {
                label: key,
                fill: true,
                lineTension: 0,
                backgroundColor: this.chooseColour(x),
                borderColor: this.chooseColour(x),
                data: commit_timeline[key]
            }
            x++;
            line_data.push(entry);
        }
        return {
            datasets: line_data,
            labels: ['Jan', 'Feb', 'March',
            'April', 'May', 'June', 'July', 'Aug','Sept', 'Oct', 'Nov', 'Dec']
        };
        
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

export default LineChart;
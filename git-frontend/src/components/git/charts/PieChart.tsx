import React from 'react';
import './App.css';

import {Pie} from 'react-chartjs-2';

class PieChart extends  React.Component<{data?: any}, {data?: any}> {
    public contributions;
    public pieData;

    constructor(props) {
        super(props);
        this.state = {data: props.data};
    }


    render() {
        if (this.state.data === null) {
            return <div></div>
        }
        else {
            let pie_data = this.getPieData();
            return (
                <div>
                    <Pie
                        data={pie_data}
                        options={{
                            borderColor: '#000000',
                            title: {
                                display: true,
                                text: 'Contribution (%)',
                                fontColor: '##FFFFFF',
                                fontSize: 20,
                                position: 'top'
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


    getPieData() {
        let contributions = {}
        for (let i = 0; i < this.state.data.length; i++) {
            if (contributions[this.state.data[i]["author"]] === undefined) {
                contributions[this.state.data[i]["author"]] = 1
            }
            else {
                contributions[this.state.data[i]["author"]]++;
            }
        }
        return {
            labels: Object.keys(contributions),
            datasets: [
                {
                    label: 'Contributions',
                    backgroundColor: this.chooseColours(Object.keys(contributions).length),
                    data: Object.values(contributions)
                }
            ]
        }
    }


    chooseColours(numberOfColours) {
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

        return colors.slice(0,numberOfColours)
    }

    componentWillReceiveProps(nextProps) {
        this.setState({data: nextProps.data});
    }
}

export default PieChart;
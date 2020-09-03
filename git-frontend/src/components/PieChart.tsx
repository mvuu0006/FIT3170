import React from 'react';
import './App.css';

import {Pie} from 'react-chartjs-2';

class PieChart extends  React.Component<{data?: any}, {data?: any}> {
    public state;
    public test
    constructor(props) {
        super(props);
        this.state=null;
        this.test={data: "{}"};
        console.log(this.test);
    }

    async componentWillMount() {
        await this.getContributions();}

    render() {
        return (
            <div>
                <Pie
                     data={this.state}
                     options={{
                         title:{
                             display:true,
                             text:'Contribution',
                             fontSize:20
                         },
                         legend:{
                             display:true,
                             position:'right'
                         }
                     }}
                />
            </div>
        );
    }



    async getContributions()
    {
        this.state={
            labels: ['January', 'February', 'March',
                'April'],
            datasets: [
                {
                    label: 'Rainfall',
                    backgroundColor: [
                        '#2FDE00',
                        '#00A6B4',
                        '#B21F00',
                        '#C9DE00',
                        '#6800B4'
                    ],

                    data: [75, 10, 5, 2]
                }
            ]
        }
    }
}

export default PieChart;
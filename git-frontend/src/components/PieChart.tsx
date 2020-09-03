import React from 'react';
import logo from './logo.svg';
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import AuthcateDisplay from './AuthcateDisplay';
import HTTPResponseDisplay from './HTTPResponseDisplay';
import { parse } from 'querystring';
import PageHandler from './PageHandler';

import {Pie, Doughnut} from 'react-chartjs-2';
import exp from "constants";
const state = {
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

class PieChart extends  React.Component<{ }, {users: any }>{
    constructor(props) {
        super(props);
        this.state = {users: null};
    }

    render() {
        return (
            <div>
                <Pie
                     data={state}
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

}

export default PieChart;
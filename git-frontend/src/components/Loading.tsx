import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";


class Loading extends React.Component<{data?: any}, {data?: any}> {
    render() {
        return (<div></div>);
    }
}

export default Loading;
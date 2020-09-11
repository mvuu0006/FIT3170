import React, { Component } from "react";
import PieChart from "./PieChart";
import LineChart from "./LineChart";
import HTTPResponseDisplay from "./HTTPResponseDisplay";
import './App.css';

class DisplayCharts extends Component<any> {

    constructor(props) {
        super(props);
        this.state = {data: null, gitInfo: null};
    }

    render() {
        return (
            <div className="Info-Page container">
                <div className="Page-Title row justify-content-md-center">
                    {this.props.history.location.state[0].RepositoryName}
                </div>
                <div className="Chart-Container row justify-content-md-center">
                    <div className="col-md-5 col-sm-12"><PieChart data = {this.props.history.location}/></div>
                    <div className="col-md-5 col-sm-12"><LineChart data = {this.props.history.location}/></div>
                </div>
                <div className="Repo-viewer row"><HTTPResponseDisplay data={this.props.history.location} /></div>
            </div>
        );
    }
}

export default DisplayCharts;
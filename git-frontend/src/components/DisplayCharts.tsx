import React, { Component } from "react";
import { Button } from 'react-bootstrap';
import history from "./history";
import exp from "constants";
import PieChart from "./PieChart";
import LineChart from "./LineChart";
import HTTPResponseDisplay from "./HTTPResponseDisplay";

class DisplayCharts extends Component<any> {

    constructor(props) {
        super(props);
        this.state = {data: null, gitInfo: null};
    }

    render() {
        return (
            <div>
                {console.log("Props")}
                {console.log(this.props)}
                <div className="Repo-chart"><PieChart data = {this.props.history.location}/></div>
                 <div className="Repo-Line"><LineChart data = {this.props.history.location}/></div>
                {/*<div className="Repo-viewer"><HTTPResponseDisplay data={this.props.history.location} /></div>*/}

            </div>
        //

        );
    }
}

export default DisplayCharts;
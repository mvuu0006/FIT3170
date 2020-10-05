import React, { Component } from "react";
import PieChartNew from "./PieChartNew";
import LineChartNew from "./LineChart";
import HTTPResponseDisplay from "./HTTPResponseDisplay";
import './App.css';

class DisplayChartsNew extends Component<{project_id: any, git_id: any}, {project_id: any, git_id: any, data: any}> {
    private projectId;
    private name;

    constructor(props) {
        super(props);
        this.state = {project_id: props.project_id, git_id: props.git_id, data: null};
    }

    render() {
        console.log(this.state);
        return (
            <div className="Info-Page container">
                <div className="Page-Title row justify-content-md-center">
                    Placeholder
                </div>
                <div className="Chart-Container row justify-content-md-center">
                   <div className="col-md-6 col-sm-12"><PieChartNew data = {this.state.data}/></div>
                    <div className="col-md-6 col-sm-12">{/*<LineChartNew data = {this.state.data}/>*/}</div>
                </div>
                <div className="Repo-viewer row"><HTTPResponseDisplay data={this.state.data} /></div>
            </div>);
    }

    async componentDidMount() {
        // Get gitlab token from localstorage
        let gitlab_token = localStorage.getItem("spmd-git-labtoken");
        // Get commits information from backend
        let url = "http://localhost:5001/git-db/project/"+this.state.project_id+"/repository/commits?";
        let params = "repo-id="+this.state.git_id+"&token="+gitlab_token+"&email="+"hbak0001@student.monash.edu"; // email is hardcoded for testing purposes
        let uri = url + params;
        const requestOptions = {
            method: 'GET'
        }
        let response = await fetch(uri, requestOptions);
        let content = await response.json();
        this.setState({data: content});
    }

}

export default DisplayChartsNew;
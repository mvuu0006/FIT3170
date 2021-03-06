import React, { Component } from "react";
import PieChart from "./charts/PieChart";
import LineChart from "./charts/LineChart";
import CommitTable from "./charts/CommitTable";

class DisplayCharts extends Component<{project_id: any, git_id: any}, {project_id: any, git_id: any, data: any, project_name?: any}> {

    constructor(props) {
        super(props);
        this.state = {project_id: props.project_id, git_id: props.git_id, data: null, project_name: ""};
    }

    render() {
        return (
            <div className="Info-Page container">
                <div className="Page-Title row justify-content-md-center">
                    {this.state.project_name}
                </div>
                <div className="Chart-Container row justify-content-md-center">
                   <div className="col-md-6 col-sm-12"><PieChart data = {this.state.data}/></div>
                    <div className="col-md-6 col-sm-12"><LineChart data = {this.state.data}/></div>
                </div>
                <div className="Repo-viewer row"><CommitTable data={this.state.data} /></div>
            </div>);
    }

    async componentDidMount() {
        // Get gitlab token from localstorage
        let gitlab_token = localStorage.getItem("spmd-git-labtoken");
        // Get commits information from backend
        let url = "http://localhost:5001/git/project/"+this.state.project_id+"/repository/commits?";
        let params = "repo-id="+this.state.git_id+"&token="+gitlab_token+"&email="+"hbak0001@student.monash.edu"; // email is hardcoded for testing purposes
        let uri = url + params;
        const requestOptions = {
            method: 'GET'
        }
        let response = await fetch(uri, requestOptions);
        let content = await response.json();
        this.setState({data: content});

        this.getRepositoryName();
    }

    async getRepositoryName() {
           // Get gitlab token from localstorage
        let gitlab_token = localStorage.getItem("spmd-git-labtoken");
        // Get commits information from backend
        let url = "http://localhost:5001/git/project/"+this.state.project_id+"/?";
        let params = "token="+gitlab_token;
        let uri = url + params;
        const requestOptions = {
            method: 'GET'
        }
        let response = await fetch(uri, requestOptions);
        let content = await response.json();
        for (let i = 0; i < content.length; i++) {
            if (content[i]["id"] === this.state.git_id) {
                this.setState({project_name: content[i]["name"]});
            }
        }
    }

}

export default DisplayCharts;
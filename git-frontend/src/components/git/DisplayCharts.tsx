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
        // Get gitlab token from sessionStorage
        let gitlab_token = sessionStorage.getItem("spmd-git-labtoken");
        let token_promise = await fetch("http://spmdgitbackend-env.eba-dyda2zrz.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/gitlab-info");
        let token_from_http = await token_promise.json();
        if (token_from_http["has-gitlab"] === "True" && token_from_http["gitlab-access-token"] !== "None") {
            gitlab_token = token_from_http["gitlab-access-token"];
            if (gitlab_token !== null) sessionStorage.setItem("spmd-git-labtoken", gitlab_token);
        }
        // Get commits information from backend
        //let url = "http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/repository/commits?";
        let url = "http://spmdgitbackend-env.eba-dyda2zrz.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/repository/commits?";
        let params = "repo-id="+this.state.git_id+"&token="+gitlab_token;
        let uri = url + params;
        let google_token = window.sessionStorage.getItem('google_id_token');
        if (google_token !== null) {
            uri += "&id_token="+google_token;
        }
        const requestOptions = {
            method: 'GET'
        }
        let response = await fetch(uri, requestOptions);
        let content = await response.json();
        this.setState({data: content});

        this.getRepositoryName();
    }

    async getRepositoryName() {
           // Get gitlab token from sessionStorage
        let gitlab_token = sessionStorage.getItem("spmd-git-labtoken");
        // Get commits information from backend
        // let url = "http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/?";
        let url = "http://spmdgitbackend-env.eba-dyda2zrz.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/?";
        let params = "token="+gitlab_token;
        let uri = url + params;
        let google_token = window.sessionStorage.getItem('google_id_token');
        if (google_token !== null) {
            uri += "&id_token="+google_token;
        }
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
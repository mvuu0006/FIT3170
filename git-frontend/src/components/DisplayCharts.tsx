import React, { Component } from "react";
import PieChart from "./PieChart";
import LineChart from "./LineChart";
import HTTPResponseDisplay from "./HTTPResponseDisplay";
import './App.css';

class DisplayCharts extends Component<any,{data: any, gitInfo: any}> {
    private projectId;
    private name;

    constructor(props) {
        super(props);
        this.name = "";
        this.state = {data: null, gitInfo: null};
    }

    render() {
        if (this.state.gitInfo != null)
        {return (
                    <div className="Info-Page container">
                        <div className="Page-Title row justify-content-md-center">
                            {this.state.gitInfo.RepositoryName}
                        </div>
                        <div className="Chart-Container row justify-content-md-center">
                            <div className="col-md-5 col-sm-12"><PieChart data = {this.state.gitInfo}/></div>
                            <div className="col-md-5 col-sm-12"><LineChart data = {this.state.gitInfo}/></div>
                        </div>
                        <div className="Repo-viewer row"><HTTPResponseDisplay data={this.state.gitInfo} /></div>
                    </div>)
                ;}
        if (this.props.history.location.state == null){return <div>Oops. It's not working yet :( </div>;}
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

    async componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    var projectId = params.get('projectId');
    var gitId = params.get('gitId');
    const projectGETOptions = {
          method: 'GET',
        };
        //var repo_response = await fetch('http://localhost:5001/git/project/'+projectId+"/repos/"+gitId, projectGETOptions);
        var repo_response = await fetch('http://localhost:5001/git/project/'+projectId+"/repos", projectGETOptions);

        var repo_data = await repo_response.json();
        if (repo_data["status"] == 404) {
          console.log("Repo GET didnt work. SAD!");
        }
        else {
          this.setState({gitInfo: repo_data[0]});}}

}

export default DisplayCharts;
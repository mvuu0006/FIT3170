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
        return <div>Oops. Something went wrong :(</div>
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
            for (let i = 0; i<repo_data.length; i++){
            if (repo_data[i].GitId == gitId){
                this.setState({gitInfo: repo_data[i]});}
            else{
                this.setState({gitInfo: repo_data[0]});}
                }}}

}

export default DisplayCharts;
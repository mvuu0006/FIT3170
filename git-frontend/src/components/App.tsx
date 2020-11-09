import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./git/DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Loading from "./Loading";
import RepoAdder from './git/RepoAdder';
import { generateKeyPair } from 'crypto';


class App extends React.Component<{window?: any, project_id?: any, git_id?: any, id_token?: any},
{window?: any, project_id?: any, git_id?: any, id_token?: any}> {
    public projects={}
    public b;

    constructor(props) {
        super(props);
        this.state = {window: "loading", project_id: null, git_id: null, id_token: props.id_token};
    }

    render() {
        console.log("rendering "+this.state.window);
        switch (this.state.window){
        case "loading":
            return (<Loading />);
        case "charts":
            return (<DisplayCharts project_id={this.state.project_id} git_id={this.state.git_id} />);
        case "adder":
            return (<RepoAdder/>);
        default:
            return (<div></div>);
        }
    }

    componentDidMount() {
        var search = window.location.search;
        var params = new URLSearchParams(search);

        let projectId : string | null = params.get('project-id');
        let gitId : string | null = params.get('git-id');

        if (projectId != null && gitId != null) {
        this.setState({window: "charts", project_id: projectId, git_id: gitId});
        }
        else if (projectId != null) {
        this.setState({window: "adder", project_id: projectId});
        }
        else {
        this.setState({window: "loading"});
        }
        
    }

    storeProject(projectInfo) {
        let gitID = projectInfo["GitId"];

        if (!(gitID in this.projects)) {
            this.projects[gitID] = projectInfo
        }
    }

}
export default App;

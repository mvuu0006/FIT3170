import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";
import Loading from "./Loading";
import RepoAdder from './RepoAdder';


class App extends React.Component<{window?: any, project_id?: any}, {window?: any, project_id?: any}> {
    public projects={}
    public b;

    constructor(props) {
      super(props);
      this.state = {window: "loading", project_id: null};
    }

    render() {
    // return (
    //     <Router history={history}>
    //       <Switch>
    //         <Route path="/DisplayCharts" exact component={DisplayCharts} />
    //           <Route path="/gitfrontend" exact component={DisplayCharts} />
    //           <Route path="/"
    //                  render={(props) => (<Home {...props}
    //                                            data={{storeProject:this.storeProject.bind(this)}} />)} />
    //       </Switch>
    //     </Router>
    // );
    console.log("rendering "+this.state.window);
    switch (this.state.window){
      case "loading":
        return (<Loading />);
      case "charts":
        return (<DisplayCharts />);
      case "adder":
        return (<RepoAdder project_id={this.state.project_id} />);
      default:
        return (<div></div>);
    }
  }

  componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    let projectId : string | null = params.get('project-id');
    let gitId : string | null = params.get('git-id');
    console.log(projectId);

    if (projectId != null && gitId != null) {
      this.setState({window: "charts", project_id: projectId});
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

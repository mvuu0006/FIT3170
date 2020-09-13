import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";


class App extends React.Component<{data?: any, gitInfo?: any}, {data?: any, gitInfo?: any}> {
    public projects={}
    public b;
    render() {
    return (
        <Router history={history}>
          <Switch>
            <Route path="/DisplayCharts" exact component={DisplayCharts} />
              <Route path="/gitfrontend" exact component={DisplayCharts} />
              <Route path="/"
                     render={(props) => (<Home {...props}
                                               data={{storeProject:this.storeProject.bind(this)}} />)} />
          </Switch>
        </Router>

    );
  }

  storeProject(projectInfo) {
      let gitID = projectInfo["GitId"];

      if (!(gitID in this.projects)) {
          this.projects[gitID] = projectInfo
      }
  }

 }
 export default App;

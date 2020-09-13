import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";
import Test from "./test";


class App extends React.Component<{data?: any, gitInfo?: any}, {data?: any, gitInfo?: any}> {
    public projects={}
    public b;
    render() {
    return (
        <Router history={history}>
          <Switch>
            <Route path="/DisplayCharts" exact component={DisplayCharts} />
              <Route path="/b"
                     render={(props) => (<Test {...props}
                                               data={{name:"Keshav",
                                               fa:this.childtoParent.bind(this)}} />)} />
              {/*<Route path="gitfrontend?project-id=${as}&git-id=234"/>*/}
              <Route path="/"
                     render={(props) => (<Home {...props}
                                               data={{storeProject:this.storeProject.bind(this)}} />)} />
          </Switch>
        </Router>

    );
  }

  childtoParent(a)
  {
      this.b=a;
      console.log(this.b)
  }

  storeProject(projectInfo) {
      let gitID = projectInfo["GitId"];

      if (!(gitID in this.projects)) {
          this.projects[gitID] = projectInfo
      }
  }

 }
 export default App;

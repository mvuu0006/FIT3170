import React from 'react';
import './App.css';
import history from "./history";
import DisplayCharts from "./DisplayCharts";
import { Router, Switch, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from "./Home";


class App extends React.Component<{data?: any, gitInfo?: any}, {data?: any, gitInfo?: any}> {
  render() {
    return (
        <Router history={history}>
          <Switch>
            <Route path="/DisplayCharts" exact component={DisplayCharts} />
              <Route path="/"  component={Home} />
          </Switch>
        </Router>

    );
  }
 }
 export default App;

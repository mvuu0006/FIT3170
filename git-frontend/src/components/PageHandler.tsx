import React from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import App from './App';

export default function PageHandler() {
    return (
        <Router>
            <Switch>
                <Route path="/">
                    <App />
                </Route>
            </Switch>
        </Router>
    );
}
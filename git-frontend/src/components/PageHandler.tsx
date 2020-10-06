import React from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import App from './App';
import Contacts from './contacts/Contacts';
import UsefulResources from './UsefulSafeResources'

export default function PageHandler() {
    return (
        <Router>
            <Switch>
                <Route path="/contacts">
                    <Contacts />
                 </Route>
                <Route path="/useful-websites-frontend">
                    <UsefulResources />
                 </Route>
                <Route path="/">
                    <App />
                </Route>
            </Switch>
        </Router>
    );
}
import React, {useReducer, FunctionComponent} from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import App from './App';
import Contacts from './contacts/Contacts';
import Login from './login/Login';
import { Integration } from "./Integration"
import AppReducer from "./state/AppReducer"
import AppInitialState from "./state/AppState"
import GoogleLoginWrapper from './GoogleLoginWrapper';

interface AppProps {
    integration: Integration
    children?: never
  }

const PageHandler: FunctionComponent<AppProps> = ({integration}) => {
    const [state, dispatch] = useReducer(AppReducer, AppInitialState)
    return (
        <Router>
            <Switch>
                <Route path="/contacts">
                    <GoogleLoginWrapper page="contact" />
                    </Route>
                <Route path="/git">
                    <GoogleLoginWrapper page="git" />
                </Route>
                <Route path="/">
                    <Login integration={integration} state={state} dispatch={dispatch} />
                </Route>
            </Switch>
        </Router>
    );
}

export default PageHandler;
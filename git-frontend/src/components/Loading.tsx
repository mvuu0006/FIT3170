import React from 'react';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useGoogleLogin } from 'react-google-login';


class Loading extends React.Component<{data?: any}, {data?: any}> {
    render() {
        return (<div></div>);
    }
}

export default Loading;
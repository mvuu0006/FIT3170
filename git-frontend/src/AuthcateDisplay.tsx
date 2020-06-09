import React from 'react';

class AuthcateDisplay extends React.Component<{authcate?: string}, {authcate?: string}> {
    constructor(props) {
        super(props);
        this.state = {authcate: "None"};
    }

    componentDidMount() {

    }

    componentWillUnmount() {

    }

    render () {
        return (<p>Current Project: {this.state.authcate}</p>);
    }

    updateAuthcate (newAuthcate) {
        this.setState({authcate: newAuthcate});
    }
}

export default AuthcateDisplay;
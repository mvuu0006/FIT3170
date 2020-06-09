import React from 'react';

class HTTPResponseDisplay extends React.Component<{data?: string}, {data?: string}> {
    constructor(props) {
        super(props);
        this.state = {data: "{}"};
    }

    componentDidMount() {

    }

    componentWillUnmount() {

    }

    render () {
        return (<div><p>Most Recent GET response:</p><p>{this.state.data}</p></div>);
    }

    updateData (data) {
        this.setState({data: data});
    }
}

export default HTTPResponseDisplay;
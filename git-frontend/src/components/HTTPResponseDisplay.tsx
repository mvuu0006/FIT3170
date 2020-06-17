import React from 'react';
import Table from 'react-bootstrap/Table';

class HTTPResponseDisplay extends React.Component<{data?: any}, {data?: any}> {
    constructor(props) {
        super(props);
        this.state = {data: "{}"};
    }

    componentDidMount() {

    }

    componentWillUnmount() {

    }

    render () {
        let header = (<div><h6>Current Project ID: {this.state.data["projectId"]}</h6></div>);
        
        if (this.state.data["repoInfo"] != null) {
            let arrayOfRepos = this.state.data["repoInfo"].map((repo) => 
            (<tr key={repo["GitId"]}>
                <td>{repo["GitId"]}</td>
                <td>{repo["UserName"]}/{repo["RepositoryName"]}</td>
                <td>{JSON.stringify(repo["commits"])}</td>
                <td>{JSON.stringify(repo["contributions"])}</td>
            </tr>));
            let table = (<Table variant="light" striped bordered hover>
                <thead>
                    <tr key="heading">
                        <th>Git ID</th>
                        <th>Repository Name</th>
                        <th>Commits</th>
                        <th>Contributions</th>
                    </tr>
                </thead>
                <tbody>
                    {arrayOfRepos}
                </tbody>
            </Table>);
            return [header, table];
        }
        return header;
    }

    updateData (data) {
        this.setState({data: data});
    }
}

export default HTTPResponseDisplay;
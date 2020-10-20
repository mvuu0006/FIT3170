import React from 'react';
import {Table} from 'react-bootstrap';

class AddedReposTable extends React.Component<{project_id?: any}, {project_id?: any, data: any}> {

    constructor(props) {
        super(props);
        this.state = {project_id: props.project_id, data: []};
    }

    render() {
        return (<Table>
            <thead>
                <tr>
                    <th>Repository Name</th>
                    <th>Repository Link</th>
                    <th>Repository ID</th>
                </tr>
            </thead>
            <tbody>
                {this.renderTableData()}
            </tbody>
        </Table>);
    }

    renderTableData() {
        return this.state.data.map((repo, index) => {
            const { service, url, id, name } = repo;
            return (
                <tr key={id}>
                    <td>{name}</td>
                    <td><a href={url}>{url}</a></td>
                    <td><a href={this.getChartURL(id)}>{id}</a></td>
                </tr>
            );
        })
    }

    getChartURL(id) : string {
        return "http://spmd-git-frontend.s3-website-ap-southeast-2.amazonaws.com/?project-id="+this.state.project_id+"&git-id="+id;
    }

    async componentDidMount() {
        const requestOptions = {
            method: 'GET'
        }
        //let url = "http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"?";+
        let url = "http://localhost:5001/git/project/"+this.state.project_id+"?";
        let token = window.sessionStorage.getItem('gl-access-token');
        let token_promise = await fetch("http://localhost:5001/git/project/"+this.state.project_id+"/gitlab-info");
        let token_from_http = await token_promise.json();
        if (token_from_http["has-gitlab"] === "True" && token_from_http["gitlab-access-token"] !== "None") {
            token = token_from_http["gitlab-access-token"];
            if (token !== null) sessionStorage.setItem("gl-access-token", token);
        }
        if (token !== null) {
            url += "token="+token;
        }
        let response = await fetch(url, requestOptions);
        let content = await response.json();
        console.log(content);
        this.setState({data: content});
    }
}

export default AddedReposTable;
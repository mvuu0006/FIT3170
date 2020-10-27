import React from 'react';
import {Table} from 'react-bootstrap';
import Button from 'react-bootstrap/Button';

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
                    <th>Remove Repository</th>
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
                    <td><Button variant="light" onClick={(e) => this.removeRepository(id)}>Remove</Button></td>
                </tr>
            );
        })
    }

    getChartURL(id) : string {
        return "http://localhost:3001/?project-id="+this.state.project_id+"&git-id="+id;
    }

    async fetchData() {
        const requestOptions = {
            method: 'GET'
        }
        let url = "http://localhost:5001/git/project/"+this.state.project_id+"?";
        let token = window.sessionStorage.getItem('gl-access-token');
        if (token !== null) {
            url += "token="+token;
        }
        let response = await fetch(url, requestOptions);
        let content = await response.json();
        this.setState({data: content});
        return content;
    }
    async componentDidMount() {
        await this.fetchData();
    }

     async removeRepository(repoID) {
        let token = window.sessionStorage.getItem('gl-access-token');
        let url = "http://localhost:5001/git/project/"+this.state.project_id+"/repository?";
        let params = "repo-id="+repoID;
        if (token !== null) {
            params += "&token="+token;
        }
        let uri = url + params;
        const requestOptions = {
            method: 'DELETE',
        }
        const promise = await fetch(uri, requestOptions);
        if (promise.status == 200){
            console.log("Repo delete successful");
            let res = await promise.json();
            await this.postToUserService(res);
            await this.fetchData();
        }
        else {
            console.log("Repo delete unsuccessful");
        }
    }

    async postToUserService(repo_info) {
        let repo_id = repo_info["repo-id"];
        // Perform POST to user service to delete repo
        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                emailAddress:"test123@gmail.com",
                gitId: repo_id.toString(),
                projectId: this.state.project_id.toString()
            })
        }
        console.log(requestOptions.body);
        let promise = await fetch("http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/remove-git", requestOptions);
        let response = await promise.json();
        if (promise.ok) {
            console.log("successfully removed id of new repo to user-service");
        }
        else {
            console.log("something went wrong when attempting to send remove git repo to user-service");
        }
        return;
    }
}

export default AddedReposTable;
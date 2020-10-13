import React, {Component} from "react";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import 'bootstrap/dist/css/bootstrap.min.css';

import AddedReposTable from './AddedReposTable';



class RepoAdder extends Component<{project_id?: any}, {project_id?: any}> {

    constructor(props) {
        super(props);
        this.state = {project_id: props.project_id};
    }

    render() {
        return (
            <div className="App">
                <div className="Page-Title"><Form.Label>Add a New Git Repository</Form.Label></div>
                <div className="Repo-adder">
                    <Form.Label>Add a Repo to Project with ID {this.state.project_id}:</Form.Label>
                    <Form onSubmit={this.addRepoButtonHandler}>
                        <div className="Form-Grid">
                            <div>
                                <Form.Group controlId="repoService">
                                    <Badge variant="secondary">Git Repository Service</Badge>
                                    <Form.Control as="select">
                                        <option>GitLab</option>
                                        <option>GitHub</option>
                                    </Form.Control>
                                </Form.Group>
                            </div>
                            <div>
                                <Form.Group controlId="repoId">
                                    <Badge variant="secondary">Repository URL</Badge>
                                    <Form.Control/>
                                </Form.Group>
                            </div>
                            <div>
                                <Button variant="light" type="submit">Submit</Button>
                            </div>
                        </div>
                    </Form>
                </div>
                <div className="Repo-list">
                    <AddedReposTable project_id={this.state.project_id}/>
                </div>
            </div>
        );
    }

    addRepoButtonHandler = async (event) => {
        event.preventDefault();

        let redirect = false;
        if (event.target.repoService.value.toLowerCase() === "gitlab"){
            redirect = true;
            this.redirectToGitLab(event.target.repoId.value);
        }

        if (!redirect){
            this.addRepoLogic(event.target.repoService.value,
                event.target.repoId.value);
        }
    }

    async addRepoLogic(service, id) {
        let token = window.sessionStorage.getItem('gl-access-token');
        // Add repo to project
        let url = "http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/repository?";
        let params = "service="+service.toLowerCase()+"&url="+id;
        if (token !== null) {
            params += "&token="+token;
        }
        let uri = url + params;
        const requestOptions = {
            method: 'POST',
        }
        const promise = await fetch(uri, requestOptions);
        const response = await promise.json();
        if (promise.status == 200){
            console.log("Repo add successful");
            this.postToUserService(response);
        }
        else {
            console.log("Repo add unsuccessful");
        }
    }

    redirectToGitLab(gitlab_url) {
        console.log("Redirecting to GL...");
        // Redirect
        let redirect_uri = "http://localhost:3001";
        if (this.state.project_id != null) {
            redirect_uri += "?project-id="+this.state.project_id;
        }
        redirect_uri += "%26git-to-add="+gitlab_url;

        window.location.href = "https://git.infotech.monash.edu/oauth/authorize" +
            "?client_id=2b2676dd243b35a0cef351c2a5a03cbf5360221219967226d4393b3715a50bef" +
            "&response_type=code" +
            "&redirect_uri="+redirect_uri;
    }

    async postToUserService(repo_info) {
        let repo_id = repo_info["repo-id"];
        let repo_service = repo_info["repo-service"];
        let repo_url = repo_info["repo-url"];
        // Perform POST to user service to add repo to their system
        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                gitName:"testName",
                emailAddress:"test123@gmail.com",
                gitId: repo_id,
                projectId: this.state.project_id
            })
        }
        let promise = await fetch("http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/save-git", requestOptions);
        let response = await promise.json();
        if (promise.ok) {
            console.log("successfully sent id of new repo to user-service");
        }
        else {
            console.log("something went wrong when attempting to send git id to user-service");
        }
        return;
    }

    async componentDidMount() {
        var search = window.location.search;
        var params = new URLSearchParams(search);

        let git_to_add : string | null = params.get('git-to-add');
        let gl_auth_code = params.get('code'); 
        let gl_access_token = window.sessionStorage.getItem('gl-access-token');

        if (gl_auth_code !== null && gl_access_token === null) {
            let response = await this.getAuthorisationCode(gl_auth_code, git_to_add);
            let token = response["access_token"];
            window.sessionStorage.setItem('gl-access-token', token);
        }

        if (git_to_add !== null) {
            this.addRepoLogic('gitlab', decodeURIComponent(git_to_add));
        }
    }

    async getAuthorisationCode(code, uri) {

        let redirect_uri = "http://localhost:3001/";
        if (this.state.project_id != null) {
            redirect_uri += "?project-id="+this.state.project_id;
        }
        redirect_uri += "%26git-to-add="+encodeURIComponent(uri);

        const requestOptions = {
            method: 'GET'
        }
        var promise = await fetch("http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/gitlab-access-code?code=" + code +
        "&redirect_uri="+encodeURIComponent(redirect_uri), requestOptions)
        var response = await promise.json();
        return response;
    }
}

export default RepoAdder;
import React, {FunctionComponent} from "react";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import 'bootstrap/dist/css/bootstrap.min.css';

import AddedReposTable from './AddedReposTable';
import { useGoogleAuth } from "../google/GoogleAuthProvider"

var idToken = null;
var email = null;

const setIdToken = (token, address) => {
    idToken = token;
    email = address;
}

const addRepoButtonHandler = async (event) => {
    event.preventDefault();

    let redirect = false;
    if (event.target.repoService.value.toLowerCase() === "gitlab"){
        redirect = true;
        redirectToGitLab(event.target.repoId.value, event.target.projectId.value);
    }

    if (!redirect){
        addRepoLogic(event.target.repoService.value,
            event.target.repoId.value, event.target.projectId.value);
    }
}

const addRepoLogic = async (service, id, project_id) => {
    let token = window.sessionStorage.getItem('gl-access-token');
    // Add repo to project
    // let url = "http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/project/"+this.state.project_id+"/repository?";
    let url = "http://spmdgitbackend-env.eba-dyda2zrz.ap-southeast-2.elasticbeanstalk.com/git/project/"+project_id+"/repository?";
    let params = "service="+service.toLowerCase()+"&url="+id;
    if (token !== null) {
        params += "&token="+token;
    }
    let uri = url + params;
    let google_token = idToken;
    console.log(google_token);
    if (google_token !== null) {
        uri += "&id_token="+google_token;
    }
    const requestOptions = {
        method: 'POST',
    }
    const promise = await fetch(uri, requestOptions);
    const response = await promise.json();
    if (promise.status == 200){
        console.log("Repo add successful");
        console.log(response);
        await postToUserService(response, project_id);
    }
    else {
        console.log("Repo add unsuccessful");
    }

    var search = window.location.search;
    var the_params = new URLSearchParams(search);

    let refresh_after_add = "http://spmd-git-frontend.s3-website-ap-southeast-2.amazonaws.com/git?"
        the_params.forEach(function(value, key) {
            if (key !== "git-to-add" && key !== "code") {
                refresh_after_add += "&" + key + "=" + value;
            }
        })
    document.location.href = refresh_after_add;
}

const redirectToGitLab = (gitlab_url, project_id) => {
    console.log("Redirecting to GL...");
    // Redirect
    let redirect_uri = "http://spmd-git-frontend.s3-website-ap-southeast-2.amazonaws.com/";
    if (project_id != null) {
        redirect_uri += "?project-id="+project_id;
    }
    redirect_uri += "%26git-to-add="+gitlab_url;

    document.location.href = "https://git.infotech.monash.edu/oauth/authorize" +
        "?client_id=2b2676dd243b35a0cef351c2a5a03cbf5360221219967226d4393b3715a50bef" +
        "&response_type=code" +
        "&redirect_uri="+redirect_uri;
}

const postToUserService = async (repo_info, project_id) => {
    let repo_id = repo_info["repo-id"];
    let repo_service = repo_info["repo-service"];
    let repo_url = repo_info["repo-url"];
    let repo_name = repo_info["repo-name"];
    // Perform POST to user service to add repo to their system
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            gitName: repo_name,
            emailAddress: email,
            gitId: repo_id,
            projectId: project_id
        })
    }
    console.log(requestOptions);
    let promise = await fetch("http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/save-git", requestOptions);
    if (promise.ok) {
        console.log("successfully sent id of new repo to user-service");
    }
    else {
        console.log("something went wrong when attempting to send git id to user-service");
    }
    return;
}

const getAuthorisationCode = async (code, uri, project_id) => {

    let redirect_uri = "http://spmd-git-frontend.s3-website-ap-southeast-2.amazonaws.com/";
    if (project_id != null) {
        redirect_uri += "?project-id="+project_id;
    }
    redirect_uri += "%26git-to-add="+encodeURIComponent(uri);

    const requestOptions = {
        method: 'GET'
    }
    // var promise = await fetch("http://spmdgitbackend-env-1.eba-knaa5ymu.ap-southeast-2.elasticbeanstalk.com/git/gitlab-access-code?code=" + code +
    // "&redirect_uri="+encodeURIComponent(redirect_uri), requestOptions)
    let url = "http://spmdgitbackend-env.eba-dyda2zrz.ap-southeast-2.elasticbeanstalk.com/git/project/"+project_id+"/gitlab-access-code?code=" + code +
    "&redirect_uri="+encodeURIComponent(redirect_uri);
    let google_token = window.sessionStorage.getItem('google_id_token');
    console.log(idToken);
    if (google_token !== null) {
        url += "&id_token="+google_token;
    }
    var promise = await fetch(url, requestOptions)
    var response = await promise.json();
    return response;
}


const RepoAdder: FunctionComponent = () => {
    const { signIn, googleUser, isInitialized, isSignedIn } = useGoogleAuth()
    const emailAddress = googleUser?.getBasicProfile()?.getEmail()
    const id_token = googleUser?.getAuthResponse().id_token;

    setIdToken(id_token, emailAddress);

    var search = window.location.search;
    var params = new URLSearchParams(search);

    let git_to_add : string | null = params.get('git-to-add');
    let gl_auth_code = params.get('code'); 
    let gl_access_token = window.sessionStorage.getItem('gl-access-token');
    let project_id = params.get('project-id'); 

    if (gl_auth_code !== null && gl_access_token === null) {
        getAuthorisationCode(gl_auth_code, git_to_add, project_id).then((data) => {
            let token = data["access_token"];
            window.sessionStorage.setItem('gl-access-token', token);
        });
    }

    if (git_to_add !== null) {
        addRepoLogic('gitlab', decodeURIComponent(git_to_add), project_id);
    }

    return (
        <div className="App">
            <div className="Page-Title"><Form.Label>Add a New Git Repository</Form.Label></div>
            <div className="Repo-adder">
                <Form.Label>Add a Repo to Project with ID {project_id}:</Form.Label>
                <Form onSubmit={addRepoButtonHandler}>
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
                        <div style={{display: "none"}}>
                            <Form.Group controlId="projectId">
                                <Form.Control as="select">
                                    <option>{project_id}</option>
                                </Form.Control>
                            </Form.Group>
                        </div>
                        <div>
                            <Button variant="light" type="submit">Submit</Button>
                        </div>
                    </div>
                </Form>
            </div>
            <div className="Repo-list">
                <AddedReposTable project_id={project_id} />
            </div>
        </div>
    );
}

export default RepoAdder;
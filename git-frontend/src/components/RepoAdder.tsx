import React, {Component} from "react";
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import 'bootstrap/dist/css/bootstrap.min.css';
import history from "./history";
import { access } from "fs";



class RepoAdder extends Component<{project_id?: any}, {project_id?: any}> {

    constructor(props) {
        super(props);
        console.log(props.project_id);
        this.state = {project_id: props.project_id};
    }

    render() {
        return (
            <div className="App">
                <div className="Page-Title"><Form.Label>Add a New Git Repository</Form.Label></div>
                <div className="Repo-adder">
                    <Form.Label>Add a Repo to Project with ID {this.state.project_id}:</Form.Label>
                    <Form onSubmit={this.addRepo}>
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
                <div className="Repo-list"></div>
            </div>
        );
    }

    addRepo = async (event) => {
        event.preventDefault();
        console.log(event.target.repoService.value);
        console.log(event.target.repoId.value);
        // Add repo to project
        let url = "http://localhost:5001/git-db/project/"+this.state.project_id+"/repository?";
        let params = "service="+event.target.repoService.value.toLowerCase()+"&url="+event.target.repoId.value;
        let uri = url + params;
        const requestOptions = {
            method: 'POST',
        }
        const promise = await fetch(uri, requestOptions);
        const response = await promise.json();
        console.log(response);
        if (promise.status == 200){
            console.log("Repo add successful");
            this.postToUserService(response);
        }
        else {
            console.log("Repo add unsuccessful");
        }
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
}

export default RepoAdder;
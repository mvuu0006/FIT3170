import React, {Component} from "react";
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import 'bootstrap/dist/css/bootstrap.min.css';
import history from "./history";
import { access } from "fs";



class Home extends Component <{data?: any, gitInfo?: any}, {data?: any, gitInfo?: any}> {
    public projectId;
    public gitLabToken;
    private repoOwner;
    private repoName;
    private gitId;

    constructor(props) {
        super(props);
        this.state = {data: null, gitInfo: null};
    }

    render() {
        return (
            <div className="App">
                <div className="Page-Title"><Form.Label>Add a New Git Repository</Form.Label></div>
                <div className="App-grid">
                    <div className="Repo-adder">
                        <Form.Label>Add a GitHub Repo to Project:</Form.Label>
                        <Form onSubmit={this.addRepo}>
                            <Form.Group controlId="repoUser">
                                <Badge variant="secondary">GitHub username</Badge>
                                <Form.Control placeholder="(eg. hbak0001)"/>
                            </Form.Group>
                            <Form.Group controlId="repoLink">
                                <Badge variant="secondary">GitHub repository name</Badge>
                                <Form.Control placeholder="(eg. fit3170-asgn1)"/>
                            </Form.Group>
                            <Button variant="light" type="submit">Submit</Button>
                        </Form>
                    </div>
                    <div className="Repo-adder-lab">
                        <Form.Label>Add a GitLab Repo to Project:</Form.Label>
                        <Form onSubmit={this.addlabRepo}>
                            <Form.Group controlId="repoLabLink">
                                <Badge variant="secondary">GitLab Project ID</Badge>
                                <Form.Control placeholder="(eg. 10273)"/>
                            </Form.Group>
                            <Button variant="light" type="submit">Submit</Button>
                        </Form>
                    </div>
                    <div className="Repo-list"></div>

                </div>
            </div>
        );
    }

    sendDataToParent() {
        this.props.data.storeProject(this.state.gitInfo[this.state.gitInfo.length - 1])
    }

    handleButtonClick() {
        var extraparam = '?project-id=' + this.projectId;
        if (this.gitId != null) {
            extraparam += '&git-id=' + this.gitId;
        }
        history.push({
            pathname: '/gitfrontend',
            search: extraparam
        });
    }

  async componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    let projectId : string | null = params.get('project-id');
    let gitId : string | null = params.get('git-id');

    // Store the project-id and git-id in localstorage before redirect
    if (projectId != null){
      localStorage.setItem("spmd-git-pid", projectId);
    }
    if (gitId != null){
      localStorage.setItem("spmd-git-gid", gitId);
    }

    var gitLabCode = params.get('code');
    /*
      Step 1: If no code is given the the url, redirect to authorise with GitLab
    */
    let redirecting = false;
    if (gitLabCode === null) {
      redirecting = true;
      console.log("Redirecting to GL...");
      // Redirect
      let redirect_uri = "http://localhost:3001";
      if (projectId != null) {
        redirect_uri += "?project-id="+projectId;
      }
      if (gitId != null) {
        redirect_uri += "%26git-id="+gitId;
      }
      window.location.href = "https://git.infotech.monash.edu/oauth/authorize" +
        "?client_id=2b2676dd243b35a0cef351c2a5a03cbf5360221219967226d4393b3715a50bef" +
        "&response_type=code" +
        "&redirect_uri="+redirect_uri;
    }
    // Retrieve the saved pid and gid from localstorage
    projectId = localStorage.getItem("spmd-git-pid");
    gitId = localStorage.getItem("spmd-git-gid");
    console.log("Retrieved from localstorage: pid = "+projectId+", gid = "+gitId);
    // Remove them from localstorage or else it will cause complications when trying to add new repo
    if (!redirecting) {
      console.log("removing the ids");
      localStorage.removeItem("spmd-git-pid");
      localStorage.removeItem("spmd-git-gid");
    }
    /*
      Step 2: Once authorisation code is received, call backend api to get access token
    */
    // Check if access-token exists in localstorage
    var accessToken;
    if (localStorage.getItem("spmd-git-labtoken") === null) {
      var response = await this.getAuthorisationCode(gitLabCode);
      // TODO: Handle scenario in which access code call returns 404 (Happens when auth code is reused)
      accessToken = response["access_token"];
      localStorage.setItem("spmd-git-labtoken", accessToken);
    }
    else {
      accessToken = localStorage.getItem("spmd-git-labtoken");
    }
    console.log(accessToken);

    this.projectId = projectId;
    //this.doGitStuff(projectId, gitId);
    
    /*
      Step 6: Store access token in component to submit to future backend calls
     */
    this.gitLabToken = accessToken;
    // Add the repo to the project then redirect (only for gitlab)
    if (gitId != null && gitId != undefined) {
      this.hackAddRepoThenRedir(gitId);
    }
  }

  async getAuthorisationCode(code) {
      const requestOptions = {
          method: 'GET'
      }
      var promise = await fetch("http://localhost:5001/git/gitlab-access-code?code=" + code, requestOptions)
      var response = await promise.json();
      return response;
  }

    changeStudent = (event) => {
        event.preventDefault();
        // May possibly add in an initial GET that checks if the user has been registered in the backend
        const requestOptions = {
            method: 'GET'
        }
        // This call to our backend api should provide us with a list of repos currently tracked by the backend
        // Fetch data from the API (replace url below with correct api call)
        fetch('http://localhost:5001/git/users?name=' + event.target.projName.value, requestOptions)
            .then(response => response.json())
    }
    public event;
    addRepo = (event) => {
        event.preventDefault();
        this.event = event;
        // Fetch data from the API (replace url below with correct api call)
        this.repoOwner = event.target.repoUser.value;
        this.repoName = event.target.repoLink.value;
        if (!(this.repoOwner == '') && !(this.repoName == '')) {
            if (this.projectId != null) {
                const requestOptions = {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        'repoName': this.repoName,
                        'projectId': this.projectId,
                        'githubUsername': this.repoOwner,
                        'gitSite': 'github'
                    }),
                }
                this.getGitHubID(this.projectId);
                fetch('http://localhost:5001/git/project/' + this.projectId + '/repos/' + this.repoOwner + "/" + this.repoName, requestOptions)
                    .then(data => {
                        this.setState({data});
                        this.postGitData(this.projectId, this.gitId)
                        this.handleButtonClick();
                    })
                    .catch(e => {
                        console.error('Error:', e)
                    });
            }
        }
    }
    addlabRepo = (event) => {
        event.preventDefault();
        // Fetch data from the API (replace url below with correct api call)
        this.gitId = event.target.repoLabLink.value;
        if (!(this.gitId == '')) {
            if (this.projectId != null) {
                const requestOptions = {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({repo: event.target.repoLabLink.value}),
                }
                if (this.gitLabToken != null) {
                    var fetchurl = 'http://localhost:5001/git/project/' + this.projectId + '/labRepos/' + this.gitLabToken + '/' + this.gitId;
                } else {
                    fetchurl = 'http://localhost:5001/git/project/' + this.projectId + '/labRepos/' + this.gitId;
                }
                fetch('http://localhost:5001/git/project/' + this.projectId + '/labRepos/' + this.gitLabToken + '/' + this.gitId, requestOptions)
                    .then(data => {
                        this.setState({data});
                        this.postGitData(this.projectId, this.gitId)
                        this.handleButtonClick();
                    })
                    .catch(e => {
                        console.error('Error:', e)
                    });
            }
        }
    }

    async getGitHubID(projID) {
        const projectGETOptions = {
            method: 'GET',
        };
        var repo_response = await fetch('http://localhost:5001/git/project/' + projID + "/repos", projectGETOptions);

        var repo_data = await repo_response.json();
        if (repo_data["status"] == 404) {
            console.log("Repo GET didnt work. SAD!");
        }
        else {
            this.gitId=repo_data[0].GitId;
            console.log("getGitHubID")
            console.log(this.gitId)
        }
    }


  async doGitStuff(projectId, gitId) {
    var receivedInfo = {"projectId":projectId,"projectGitId":gitId};
    // See if project is already registered
    const projectGETOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    var init_response = await fetch('http://localhost:5001/git/project/'+receivedInfo["projectId"], projectGETOptions)
    var init_data = await init_response.json();
    if (init_data["status"] == 404) {
      await this.createNewProject(receivedInfo);
    }

    // Add repos to the project
    await this.addGitToProject(receivedInfo["projectGitId"], receivedInfo["projectId"]);
  }

  async createNewProject(projectData) {
    var body = {'projectId': projectData["projectId"], 'projectName': projectData["projectName"]};
    const projectPUTOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body),
    }
    let response = await fetch('http://localhost:5001/git/project', projectPUTOptions)
    .catch(error => {
      console.error('Error:',error)
    });
    if (!(response["status"] == 201)) {
      console.log("Something went wrong in creating a new project");
    };
  }

  async addGitToProject(gitId, projectId) {
    var body = {'repoId': gitId, 'projectId': projectId, 'gitSite': 'github'};
    const projectPUTOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body),
    }
    var response = await fetch('http://localhost:5001/git/project/'+(projectId as string)+'/repos/addRepofromID', projectPUTOptions)
    .catch(error => {
      console.error('Error:',error)
    });
    if (response["status"] == 201) {
      console.log("Repo with ID: "+gitId+" successfully added to Project with ID: "+projectId);
    }
    else if (response["status"] == 400) {
      var message = "Repo with ID: "+gitId+" failed to be added to Project with ID: "+projectId;
      message = message + "\nPerhaps the repo ID does not exist?";
      console.log(message);
    }
  }

  presentProjectRepos(projectId) {
    const requestOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    fetch('http://localhost:5001/git/project/'+(projectId as string)+'/repos', requestOptions)
    .then(response => response.json())
    .catch(error => {

    });
  }

  async hackAddRepoThenRedir(gitid) {
    // *******************************
    // Adding Project to backend
    // *******************************
    var receivedInfo = {"projectId":this.projectId,"projectGitId":gitid};
    // See if project is already registered
    const projectGETOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    var init_response = await fetch('http://localhost:5001/git/project/'+receivedInfo["projectId"], projectGETOptions)
    var init_data = await init_response.json();
    if (init_data["status"] == 404) {
      await this.createNewProject(receivedInfo);
    }
    // *******************************
    // Adding Repo to backend
    // *******************************
    this.gitId = gitid;
    if (this.projectId != null){
      const requestOptions = {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ repo:  this.gitId }),
      }
      if (this.gitLabToken != null) {
      var fetchurl = 'http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitLabToken+'/'+this.gitId;}
      else {fetchurl = 'http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitId;}
      console.log(this.gitLabToken);
      try {
        let data = await fetch('http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitLabToken+'/'+this.gitId ,requestOptions);
        this.postGitData(this.projectId, this.gitId);
        this.handleButtonClick();
      }
      catch (error){
        console.error(error);
      }
      
      var a=0;
    }
  }


  postGitData(projectId, gitId){
          const request = {
              method: "POST",
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                  gitName:"testName",
                  emailAddress:"test123@gmail.com",
                  gitId: gitId,
                  projectId: projectId
              })
          }
          return fetch("http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/save-git", request).then(
              response => {
                  if (response.ok) {
                      console.log(`Returned integration id successfully? Hooray?'`)
                  } else {
                      response.text()
                          .then(JSON.parse)
                          .then(result => console.log(result.message))
                  }
              }
          )
      }

}

export default Home;
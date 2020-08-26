import React from 'react';
import logo from './logo.svg';
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import AuthcateDisplay from './AuthcateDisplay';
import HTTPResponseDisplay from './HTTPResponseDisplay';
import { parse } from 'querystring';
import PageHandler from './PageHandler';

class App extends React.Component {
  public authcateDisplayElement;
  public lastGetResponse;
  public projectId;
  public gitLabToken;

  constructor(props) {
    super(props);
    this.authcateDisplayElement = React.createRef();
    this.lastGetResponse = React.createRef();
    this.state = {data: null};
  }

  render() {
    return (
      <div className="App">
        <div className="App-grid">
          <div className="Student-selector">
            {/*<Form onSubmit={this.changeStudent}>
              <Form.Label>Select Project</Form.Label>
              <Form.Group controlId="projName">
                <Badge variant="secondary">Project Name</Badge>
                <Form.Control placeholder="(eg. My Project)"/>
              </Form.Group>
              <Button variant="primary" type="submit">Submit</Button>
            </Form>*/}
            <Form.Label>Current Task</ Form.Label>
            <AuthcateDisplay ref={this.authcateDisplayElement} />
            <Button variant="primary" disabled>Click Me!</Button>
          </div>
          <div></div>
          <div className="Repo-adder">
            <Form.Label>Add a Repo to Project:</Form.Label>
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
          <div className="Repo-list">
          </div>
          <div className="Repo-viewer"><HTTPResponseDisplay ref={this.lastGetResponse} /></div>
        </div>
      </div>
    );
  }

  async componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    var projectId = params.get('project');
    var gitId = params.get('gitId');

    var gitLabCode = params.get('code');
    /* 
      Step 1: If no code is given the the url, redirect to authorise with GitLab
    */
    if (gitLabCode === null) {
      window.location.href = "https://git.infotech.monash.edu/oauth/authorize" + 
        "?client_id=25202383ac02265444e0ea55882782b3f85ba6baf53da0565652b3f9054613dc" + 
        "&response_type=code" + 
        "&redirect_uri=http://localhost:3001";
    }
    /*
      Step 2: Once authorisation code is received, call backend api to get access token
    */
    var response = await this.getAuthorisationCode(gitLabCode);
    // TODO: Handle scenario in which access code call returns 404 (Happens when auth code is reused)
    var accessToken = response["access_token"];

    this.projectId = projectId;
    this.doGitStuff(projectId, gitId);
    /*
      Step 6: Store access token in component to submit to future backend calls 
     */
    this.gitLabToken = accessToken;
    
  }

  async getAuthorisationCode(code) {
    const requestOptions = {
      method: 'GET'
    }
    var promise = await fetch("http://localhost:5001/git/gitlab-access-code?code=" + code ,requestOptions)
    var response = await promise.json();
    return response;
  }

  changeStudent = (event) => {
    event.preventDefault();
    this.authcateDisplayElement.current.updateAuthcate(event.target.projName.value);
    // May possibly add in an initial GET that checks if the user has been registered in the backend
    const requestOptions = {
      method: 'GET'
    }
    // This call to our backend api should provide us with a list of repos currently tracked by the backend
    // Fetch data from the API (replace url below with correct api call)
    fetch('http://localhost:5001/git/users?name='+event.target.projName.value, requestOptions)
      .then(response => response.json())
      .then(data => {
        //this.lastGetResponse.current.updateData(JSON.stringify(data));
      });
  }

  addRepo = (event) => {
    event.preventDefault();
    // Fetch data from the API (replace url below with correct api call)
    var repoOwner = event.target.repoUser.value;
    var repoName = event.target.repoLink.value;
    if (!(repoOwner == '') && !(repoName == '')) {
      if (this.projectId != null){
        const requestOptions = {
          method: 'PUT',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ repo:  event.target.repoLink.value }),
        }
        fetch('http://localhost:5001/git/project/'+this.projectId+'/repos/'+repoOwner+'/'+repoName ,requestOptions)
          .then(response => {
            this.authcateDisplayElement.current.updateAuthcate();
          })
          .then(data => {
            this.setState({data});
            this.updateTable();
          })
          .catch(e => { console.error('Error:', e) });
      }
    }
  }

  async doGitStuff(projectId, gitId) {
    var receivedInfo = {"projectId":projectId,"projectGitId":gitId};
    // See if project is already registered
    const projectGETOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    this.authcateDisplayElement.current.updateAuthcate("Attempting to GET project");
    var init_response = await fetch('http://localhost:5001/git/project/'+receivedInfo["projectId"], projectGETOptions)
    var init_data = await init_response.json();
    if (init_data["status"] == 404) {
      await this.createNewProject(receivedInfo);
    }
    // Add repos to the project
    await this.addGitToProject(receivedInfo["projectGitId"], receivedInfo["projectId"]);
    // Display Project Information
    this.updateTable();
  }

  async updateTable() {
    const projectGETOptions = {
      method: 'GET',
    };
    var repo_response = await fetch('http://localhost:5001/git/project/'+this.projectId+"/repos", projectGETOptions);
    var repo_data = await repo_response.json();
    if (repo_data["status"] == 404) {
      console.log("Repo GET didnt work. SAD!");
    }
    else {
      var allInfo = {projectId: this.projectId, repoInfo: repo_data};
      // Display Info
      this.lastGetResponse.current.updateData(allInfo);
    }
  }

  async createNewProject(projectData) {
    var body = {'projectId': projectData["projectId"], 'projectName': projectData["projectName"]};
    this.authcateDisplayElement.current.updateAuthcate("Attempting to PUT project");
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
    const projectPUTOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
    }
    this.authcateDisplayElement.current.updateAuthcate("Attempting to PUT repository");
    var response = await fetch('http://localhost:5001/git/project/'+(projectId as string)+'/repos/'+(gitId as string), projectPUTOptions)
    .catch(error => {
      console.error('Error:',error)
    });
    if (response["status"] == 201) {
      console.log("Repo with ID: "+gitId+" successfully added to Project with ID: "+projectId);
      this.authcateDisplayElement.current.updateAuthcate("None");
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
    .then(data => {
      //this.lastGetResponse.current.updateData(JSON.stringify(data));
    })
    .catch(error => {

    });
  }
}

export default App;

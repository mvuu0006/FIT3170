import React, {Component} from "react";
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import 'bootstrap/dist/css/bootstrap.min.css';
import history from "./history";



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

    sendDataToParent()
    {
        this.props.data.storeProject(this.state.gitInfo)
    }

  handleButtonClick()
    {
    var extraparam = '?project-id='+this.projectId;
    if (this.gitId != null){extraparam += '&git-id='+this.gitId;}
      history.push({
        pathname: '/DisplayCharts',
        search:extraparam
      });
    }

      async componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    var projectId = params.get('project-id');
    var gitId = params.get('git-id');

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
    // May possibly add in an initial GET that checks if the user has been registered in the backend
    const requestOptions = {
      method: 'GET'
    }
    // This call to our backend api should provide us with a list of repos currently tracked by the backend
    // Fetch data from the API (replace url below with correct api call)
    fetch('http://localhost:5001/git/users?name='+event.target.projName.value, requestOptions)
      .then(response => response.json())
  }
   public event;
  addRepo = (event) => {
    event.preventDefault();
    this.event=event;
    // Fetch data from the API (replace url below with correct api call)
    this.repoOwner = event.target.repoUser.value;
    this.repoName = event.target.repoLink.value;
    if (!(this.repoOwner == '') && !(this.repoName == '')) {
      if (this.projectId != null){
        const requestOptions = {
          method: 'PUT',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ 'repoName':  this.repoName, 'projectId': this.projectId, 'githubUsername': this.repoOwner,'gitSite': 'github' }),
        }
        fetch('http://localhost:5001/git/project/'+this.projectId+'/repos/'+ this.repoOwner+"/"+this.repoName ,requestOptions)
          .then(data => {
            this.setState({data});
            this.handleButtonClick();
          })
          .catch(e => { console.error('Error:', e) });
        // this.handleButtonClick();
      }
    }
  }
  addlabRepo = (event) => {
    event.preventDefault();
    // Fetch data from the API (replace url below with correct api call)
    this.gitId = event.target.repoLabLink.value;
    if (!(this.gitId == '')) {
      if (this.projectId != null){
        const requestOptions = {
          method: 'PUT',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({ repo:  event.target.repoLabLink.value }),
        }
        if (this.gitLabToken != null) {
        var fetchurl = 'http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitLabToken+'/'+this.gitId;}
        else {fetchurl = 'http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitId;}
        fetch('http://localhost:5001/git/project/'+this.projectId+'/labRepos/'+this.gitLabToken+'/'+this.gitId ,requestOptions)
          .then(data => {
            this.setState({data});
              this.handleButtonClick();
          })
          .catch(e => { console.error('Error:', e) });
        var a=0;
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

}

export default Home;
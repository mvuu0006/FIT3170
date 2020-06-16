import React from 'react';
//import logo from './logo.svg';
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import AuthcateDisplay from './AuthcateDisplay';
import HTTPResponseDisplay from './HTTPResponseDisplay';

class App extends React.Component {
  public authcateDisplayElement;
  public lastGetResponse;

  constructor(props) {
    super(props);
    this.authcateDisplayElement = React.createRef();
    this.lastGetResponse = React.createRef();
    this.state = {data: null,}
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
            <Button variant="primary" onClick={this.doExample}>Click Me!</Button>
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
              <Button variant="light" type="submit" disabled>Submit</Button>
            </Form>
          </div>
          <div className="Repo-list">
            <HTTPResponseDisplay ref={this.lastGetResponse} />
          </div>
          <div className="Repo-viewer"></div>
        </div>
      </div>
    );
  }

  componentDidMount() {
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
    fetch('http://localhost:8080/git/users?name='+event.target.projName.value, requestOptions)
      .then(response => response.json())
      .then(data => {
        this.lastGetResponse.current.updateData(JSON.stringify(data));
      });
  }

  addRepo = (event) => {
    event.preventDefault();
    // Fetch data from the API (replace url below with correct api call)
    const requestOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({ repo:  event.target.repoLink.value }),
    }
    fetch('http://localhost:8080/api' ,requestOptions)
      .then(response => {
        this.authcateDisplayElement.current.updateAuthcate();
      })
      .then(data => this.setState({data}));
  }

  doExample = (event) =>{
    event.preventDefault();
    // Fetch data from the API (replace url below with correct api call)
    /*const requestOptions = {
      method: 'GET',
      mode: 'no-cors' as "no-cors",
      headers: {'Content-Type': 'application/json'},
    }
    fetch('http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/get-project?email=test123&projectId=2' ,requestOptions)
    .then(response => {
      console.log("SED")
      response.json()
    })
    .then(data => {
      console.log(JSON.stringify(data));
    });*/
    // This var should be removed and the following code should be put into the second .then once the CORS issue is worked out
    var testData = {"projectId":"2","projectName":"TestProject2","projectGitIds":["1","1234","tesre22","tesre223","testGoogle","testGoogle2"],"projectGoogleDriveIds":["0AMHqlwMzue81Uk9PVA","1","tesre222223","tesre223","test","test1","testGoogle","testGoogle2"],"projectGoogleFolderIds":["test1","testGoogle","testGoogle2"],"projectTrelloIds":["1","2","50","501","tesre22","tesre222","testGoogle","testGoogle2"]};
    // See if project is already registered
    const projectGETOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    this.authcateDisplayElement.current.updateAuthcate("Attempting to GET project");
    fetch('http://localhost:5001/git/project/'+testData["projectId"], projectGETOptions)
    .then(response => response.json())
    .then(data => {
      if (data["status"] == 404) {
        this.createNewProject(testData);
      }
      else {
        this.authcateDisplayElement.current.updateAuthcate("None");
        this.presentProjectRepos(testData["projectId"]);
      }
    });
  }

  createNewProject(projectData) {
    var body = {'projectId': projectData["projectId"], 'projectName': projectData["projectName"]};
    this.authcateDisplayElement.current.updateAuthcate("Attempting to PUT project");
    const projectPUTOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body),
    }
    fetch('http://localhost:5001/git/project', projectPUTOptions)
    .then(response => {
      if (response["status"] == 201) {
        for (var i = 0; i < projectData["projectGitIds"].length; i++) {
          this.addGitToProject(projectData["projectGitIds"][i], projectData["projectId"]);
        }
      }
    })
    .catch(error => {
      console.error('Error:',error)
    });
  }

  addGitToProject(gitId, projectId) {
    const projectPUTOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
    }
    this.authcateDisplayElement.current.updateAuthcate("Attempting to PUT repository");
    fetch('http://localhost:5001/git/project/'+(projectId as string)+'/repos/'+(gitId as string), projectPUTOptions)
    .then(response => {
      if (response["status"] == 201) {
        console.log("Repo with ID: "+gitId+" successfully added to Project with ID: "+projectId);
        this.authcateDisplayElement.current.updateAuthcate("None");
      }
      else if (response["status"] == 400) {
        var message = "Repo with ID: "+gitId+" failed to be added to Project with ID: "+projectId;
        message = message + "\nPerhaps the repo ID does not exist?";
        console.log(message);
      }
    })
    .catch(error => {
      console.error('Error:',error)
    });
  }

  presentProjectRepos(projectId) {
    const requestOptions = {
      method: 'GET',
      headers: {'Content-Type': 'application/json'},
    }
    fetch('http://localhost:5001/git/project/'+(projectId as string)+'/repos', requestOptions)
    .then(response => response.json())
    .then(data => {
      this.lastGetResponse.current.updateData(JSON.stringify(data));
    })
    .catch(error => {

    });
  }
}

export default App;

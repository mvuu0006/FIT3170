import React from 'react';
import logo from './logo.svg';
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
            <Form onSubmit={this.changeStudent}>
              <Form.Label>Select Project</Form.Label>
              <Form.Group controlId="projName">
                <Badge variant="secondary">Project Name</Badge>
                <Form.Control placeholder="(eg. My Project)"/>
              </Form.Group>
              <Button variant="primary" type="submit">Submit</Button>
            </Form>
            <AuthcateDisplay ref={this.authcateDisplayElement} />
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
}

export default App;

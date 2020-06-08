import React from 'react';
import logo from './logo.svg';
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import AuthcateDisplay from './AuthcateDisplay';

class App extends React.Component {
  public authcateDisplayElement;

  constructor(props) {
    super(props);
    this.authcateDisplayElement = React.createRef();
    this.state = {data: null,}
  }

  render() {
    return (
      <div className="App">
        <div className="App-grid">
          <div className="Student-selector">
            <Form onSubmit={this.changeStudent}>
              <Form.Group controlId="studentAuthcate">
                <Form.Label>Authcate</Form.Label>
                <Form.Control placeholder="(eg. hbak0001)"/>
              </Form.Group>
              <Button variant="primary" type="submit">Submit</Button>
            </Form>
            <AuthcateDisplay ref={this.authcateDisplayElement} />
          </div>
          <div className="Repo-adder">
            <Form onSubmit={this.addRepo}>
              <Form.Group controlId="repoLink">
                <Form.Label>Link to git repository</Form.Label>
                <Form.Control/>
              </Form.Group>
              <Button variant="light" type="submit">Submit</Button>
            </Form>
          </div>
          <div className="Repo-list"></div>
          <div className="Repo-viewer"></div>
        </div>
      </div>
    );
  }

  componentDidMount() {
    console.log("Swag");
  }

  changeStudent = (event) => {
    event.preventDefault();
    this.authcateDisplayElement.current.updateAuthcate(event.target.studentAuthcate.value);
    // Fetch data from the API (replace url below with correct api call)
    fetch('http://localhost:8080/api')
      .then(response => response.json())
      .then(data => this.setState({data}));
  }

  addRepo = (event) => {
    event.preventDefault();
    // Fetch data from the API (replace url below with correct api call)
    fetch('http://localhost:8080/api')
      .then(response => response.json())
      .then(data => this.setState({data}));
  }
}

export default App;

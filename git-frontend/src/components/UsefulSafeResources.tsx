import React from "react";
import './App.css';
import Form from 'react-bootstrap/Form';
import Table from 'react-bootstrap/Table';

class UsefulResources extends React.Component {
  // Rendering the useful safe resources page
  render() {
    return (
      <div className="Info-Page">
                <div className="Page-Title">
                  <Form.Label>Useful Resources</ Form.Label></div>
                  <div className="Repo-adder">
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>Topic</th>
                  <th>Links</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Agile</td>
                  <td><a target="_blank" rel="noopener noreferrer" href={"https://www.atlassian.com/agile"}>What is Agile?</a><div><a target="_blank" rel="noopener noreferrer" href={"https://agilemanifesto.org"}>The Agile Manifesto</a></div></td>
                </tr>
                <tr>
                  <td>SAFe</td>
                  <td><a target="_blank" rel="noopener noreferrer" href={"https://www.scaledagileframework.com"}>Scaled Agile Framework</a></td>
                </tr>
                <tr>
                  <td>Scrum</td>
                  <td><a target="_blank" rel="noopener noreferrer" href={"https://www.scrum.org/resources/what-is-scrum"}>What is Scrum?</a><div><a target="_blank" rel="noopener noreferrer" href={"https://scrumprimer.org/scrumprimer20_small.pdf"}>Lightweight Guide to Theory and Practice of Scrum</a></div></td>
                </tr>
                <tr>
                  <td>UML</td>
                  <td><a rel="noopener noreferrer" href={"https://www.alexandriarepository.org/module/uml-class-diagram-syntax"} target="_blank">UML Class Diagram Syntax</a></td>
                </tr>
                <tr>
                  <td>It's a Surprise!</td>
                  <td><a target="_blank" rel="noopener noreferrer" href={"https://www.thehappybroadcast.com/"}>¯\_(ツ)_/¯ </a></td>
                </tr>
              </tbody>
            </Table>
      </div></div>
    );
  }
}

export default UsefulResources;

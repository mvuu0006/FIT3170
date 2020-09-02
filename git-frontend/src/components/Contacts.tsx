import React from 'react';
import logo from './logo.svg';
import './App.css';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Badge from 'react-bootstrap/Badge';
import AuthcateDisplay from './AuthcateDisplay';
import { parse } from 'querystring';
import PageHandler from './PageHandler';
import Table from 'react-bootstrap/Table';
import ContactComponents from './ContactComponents';

class Contacts extends React.Component <{}, {users: any}>{
  public projectId;
  public emailaddress;
  public projectName;
  //

  constructor(props) {
    super(props);
    this.state = {users: null};
    this.projectName = "";

  }

  render() {
    return (
      <div className="Contacts">
        <div className="Contacts-grid">
          <div className="Student-selector">
            <Form.Label><h2>Contacts Page </h2></ Form.Label>
            <h6>Project Name: {this.projectName}</h6>
            <ContactComponents users={this.state.users}/>
           </div>
        </div>
      </div>
    );
  }

  async componentDidMount() {
  if (this.state.users == null) { this.getUsers();
  }}

  async getUsers() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    // Current test params are project=2&email=testemail@gmail.com
    this.projectId = params.get('project');
    var emailaddress = params.get('email');
    if (this.projectId != null){
        var projectmemberslink = "http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/get-projectusers?email="+emailaddress+"&projectId="+this.projectId;
         const projectGETOptions = {
              method: 'GET',
              headers: {'Content-Type': 'application/json'},
            }
            var init_response = await fetch(projectmemberslink, projectGETOptions)
            var init_data = await init_response.json();
            if (init_data["status"] != 404) {

                this.projectName = init_data.projects.projectName;
                this.setState({users: init_data});
            }
            else {console.log("Boo. Something went wrong :(");}
         }

  }


}

export default Contacts;

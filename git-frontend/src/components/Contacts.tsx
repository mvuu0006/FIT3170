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

class Contacts extends React.Component {
  public projectId;
  public emailaddress;

  constructor(props) {
    super(props);
    this.state = {data: null};

  }

  render() {
    return (
      <div className="Contacts">
        <div className="Contacts-grid">
          <div className="Student-selector">
            <Form.Label><h2>Contacts Page </h2></ Form.Label>
            </div><div>
            <Form.Label><h3>Go team. Yeah!</h3></ Form.Label>
          </div>
          <div><h6>Current Project ID: {this.projectId}</h6></div>
          <div></div>
        </div>
      </div>
    );
  }

  async componentDidMount() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    // Current test params are project=2&email=testemail@gmail.com
    var projectId = params.get('project');
    var emailaddress = params.get('email');
    console.log(projectId);
    if (projectId != null){
        var projectmemberslink = "http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/get-projectusers?email="+emailaddress+"&projectId="+projectId;
         const projectGETOptions = {
              method: 'GET',
              headers: {'Content-Type': 'application/json'},
            }
            var init_response = await fetch(projectmemberslink, projectGETOptions)
            var init_data = await init_response.json();
            if (init_data["status"] != 404) {
              var data = JSON.stringify(init_data);
              console.log(data);
            }
            else {console.log("Boo. Something went wrong :(");}
         }

  }


}

export default Contacts;

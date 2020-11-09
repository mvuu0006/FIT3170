import React from 'react';
import Form from 'react-bootstrap/Form';
import ContactComponents from './ContactComponents';

class Contacts extends React.Component <{email: any}, {users: any, projectId: any, projectName: any}>{
  public projectId;
  public emailaddress;
  public projectName;
  //

  constructor(props) {
    super(props);
    this.state = {users: null, projectId: null, projectName: null};
    this.projectName = "";
    console.log(props.email);
  }

  render() {
    return (
      <div className="Info-Page">
                <div className="Page-Title">
                  <Form.Label>Contacts Page</ Form.Label>
                  <h6>Project Name: {this.projectName}</h6>
                  <h6>Project Id: {this.projectId}</h6></div>
                  <div className="Repo-adder">
            <ContactComponents users={this.state.users} projectName={this.state.projectName} projectId = {this.state.projectId}/>
      </div></div>
    );
  }

  async componentDidMount() {
  if (this.state.users == null) { this.getUsers();
  }}

  async getUsers() {
    var search = window.location.search;
    var params = new URLSearchParams(search);

    // Current test params are project=2&email=testemail@gmail.com
    this.projectId = params.get('project-id');
    this.setState({projectId: this.projectId});
    var emailaddress = this.props.email;
    if (this.projectId != null){
        var projectmemberslink = "http://spmdhomepage-env.eba-upzkmcvz.ap-southeast-2.elasticbeanstalk.com/user-project-service/get-projectusers?requestorEmail="+emailaddress+"&projectId="+this.projectId;
         const projectGETOptions = {
              method: 'GET',
              headers: {'Content-Type': 'application/json'},
            }
            var init_response = await fetch(projectmemberslink, projectGETOptions)
            var init_data = await init_response.json();
            if (init_data["status"] != 404) {
                if (init_data.users != null) {
                           this.projectName = init_data.projects.projectName;
                           this.setState({users: init_data});
                           this.setState({projectName: this.projectName});}
            }
            else {console.log("Boo. Something went wrong :(");}
         }

  }


}

export default Contacts;

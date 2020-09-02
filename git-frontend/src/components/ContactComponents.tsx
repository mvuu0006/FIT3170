import React from 'react';
import Table from 'react-bootstrap/Table';

class ContactComponents extends React.Component<{users: any}, {users: any}>{
    constructor(props) {
        super(props);
    }
    componentDidMount() {

    }

    componentWillUnmount() {

    }

    render () {
            if (this.props.users != null) {
                let arrayOfPeople = this.props.users.users.map((repo) =>
                (<tr key={repo.emailAddress}>
                    <td>{repo.firstName} {repo.lastName}</td>
                    <td>{repo.emailAddress}</td>
                    <td>{repo.userGroup}</td>
                </tr>));
                let table = (<Table variant="light" striped bordered hover>
                    <thead>
                        <tr key="heading">
                            <th>Name</th>
                            <th>Email</th>
                            <th>UserGroup</th>
                        </tr>
                    </thead>
                    <tbody>
                        {arrayOfPeople}
                    </tbody>
                </Table>);
                return table;
            }
            return <div></div>;
        }

//     createHtml(){
//     for (let i = 0; i < this.props.users.users["length"]; i++)){
//             html += <div><h4>{this.props.users.users[i].firstName} {this.props.users.users[i].lastName}</h4><h5>{this.props.users.users[i].emailAddress}</h5></div>;}
//     return html}

}

export default ContactComponents;
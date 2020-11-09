import React from 'react';
import Table from 'react-bootstrap/Table';

class ContactComponents extends React.Component<{users: any, projectId: any, projectName: any}, {}>{
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
                    <td><a href={"mailto:"+ repo.emailAddress+"?Subject="+ this.props.users.projects.projectUnitCode+ ": " + this.props.projectName + ", " + this.props.projectId} target="_top">
                                {repo.emailAddress}</a></td>
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
}

export default ContactComponents;
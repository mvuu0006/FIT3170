import React from 'react';
import {MDBDataTable} from 'mdbreact';

class HTTPResponseDisplay extends React.Component<{data?: any}, {data?: any}> {
    private tableData;
    constructor(props) {
        super(props);
        this.state = {data: "{}"};
    }

    render () {
        const test_data = {
            columns: [
                {
                    label: '#',
                    field: 'id',
                                    },
                {
                    label: 'First',
                    field: 'first',

                },
                {
                    label: 'Last',
                    field: 'last',
                    //sort: 'asc'
                },
                {
                    label: 'Handle',
                    field: 'handle',
                    //sort: 'asc'
                }
            ],
            rows: [
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
                {
                    'id': 1,
                    'first': 'Mark',
                    'last': 'Otto',
                    'handle': '@mdo'
                },
                {
                    'id': 2,
                    'first': 'Jacob',
                    'last': 'Thornton',
                    'handle': '@fat'
                },
            ]
        };
        console.log(this.state)
        if (this.state.data!="{}" && this.state.data["repoInfo"].length!=0) {
            this.formatTableData()
        }
        return (<MDBDataTable
                scrollY
                maxHeight="300px"
                striped
                bordered
                small
                //data={test_data}
                 data={this.tableData}
            />
        )
    }

    formatTableData(){
        let row=new Array();


        for(let i=0;i<this.state.data["repoInfo"][0]["tableData"].length;i++)
        {
            let row_element={
                'author':this.state.data["repoInfo"][0]["tableData"][i]["name"],
                'commitMessage':this.state.data["repoInfo"][0]["tableData"][i]["commit_description"],
                'dateAndTime':this.state.data["repoInfo"][0]["tableData"][i]["date"]
            }

            row.push(row_element);
        }
        this.tableData={
            columns:[
                {
                    label: 'Author',
                    field: 'author'
                },
                {
                    label: 'Commit Message',
                    field: 'commitMessage'
                },
                {
                    label: 'Commit Date and Time (GMT)',
                    field:'dateAndTime'
                }
                ],
            rows:row
        };
    }
    updateData (data) {
        this.setState({data: data});
    }
}

export default HTTPResponseDisplay;
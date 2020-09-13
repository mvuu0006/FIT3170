import React from 'react';
import {MDBDataTable} from 'mdbreact';

class HTTPResponseDisplay extends React.Component<any> {
    public tableData;
    constructor(props) {
        super(props);
    }

    render () {
        if (this.props.data == null){return <div></div>}
        if (this.props.data.state == null){
                if (this.props.data.length!=0) {
                            console.log("table");
                            console.log(this.props);
                        //     if(this.props.data.state[0].length!=0) {
                            this.formatTableData()
                        //     }
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
                )}
        return <div></div>
    }


        formatTableData(){
            let row=new Array();


            for(let i=0;i<this.props.data.tableData.length;i++)
            {
                let row_element={
                    'author':this.props.data.tableData[i]["name"],
                    'commitMessage':this.props.data.tableData[i]["commit_description"],
                    'dateAndTime':this.props.data.tableData[i]["date"]
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
}

export default HTTPResponseDisplay;

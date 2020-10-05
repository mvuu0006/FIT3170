import React from 'react';
import {MDBDataTable} from 'mdbreact';

class HTTPResponseDisplay extends React.Component< {data?: any}, {data?: any}> {
    public tableData;
    constructor(props) {
        super(props);
        console.log(props);
        this.state = {data: props.data};
    }


    render() {
        if (this.state.data === null) { // No data to tabulate
            return(<div>Table was unable to render</div>)
        }
        else {
            let table_data = this.getTableData();
            return(
                <MDBDataTable
                        scrollY
                        maxHeight="300px"
                        striped
                        bordered
                        small
                         data={table_data}
                    />
            );
        }
    }


    getTableData(){
        let row=new Array();
        for(let i=0;i<this.state.data.length;i++)
        {
            let row_element={
                'author':this.state.data[i]["author"],
                'commitMessage':this.props.data[i]["message"],
                'dateAndTime':this.props.data[i]["date"]
            }

            row.push(row_element);
        }
        return({
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
        });
    }

    componentWillReceiveProps(nextProps) {
        this.setState({data: nextProps.data});
    }
}

export default HTTPResponseDisplay;

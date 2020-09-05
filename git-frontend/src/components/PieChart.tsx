import React from 'react';
import './App.css';

import {Pie} from 'react-chartjs-2';

class PieChart extends  React.Component<any> {
    public contributions;
    public test;
    constructor(props) {
        super(props);
        //this.state=props.gitData;
        //console.log(this.state)

        //console.log("Propsaa");
        //console.log(props.data);
        //this.contributions=this.props.data["current"];
        // if (this.contributions != null)
        // {
        //     console.log("Extractor")
        //     console.log(this.contributions);
        // }
        //this.state = {data: "{}"};
        //this.contributions=this.state.data;

    }


    async componentWillMount() {
        // const projectGETContributions = {
        //     method: 'GET',
        // }
        // var repo_response = await fetch('http://localhost:5001/git/project/'+'2'+"/repos", projectGETContributions);
        // var repo_check = await repo_response.json();
        // //if (repo_check !=undefined)
        // //{
        //     var repo_data=repo_check;
        // //}
        // if (repo_data["status"] == 404) {
        //     console.log("Repo GET didnt work. SADDD!");
        // }
        // else {
        //         console.log("Repo Data");
        //         console.log(repo_data[0]["contributions"]);
        //         this.contributions=repo_data[0]["contributions"];
        //
        //         console.log(Object.keys(this.contributions));
        //
        //     //var allInfo = {projectId: this.projectId, repoInfo: repo_data};
        //     //Display Info
        //     //this.lastGetResponse.current.updateData(allInfo);
            await this.getContributions()
        }




    render() {
        console.log(this.props.data);
        return (
            <div>
                {/*<Pie*/}
                {/*     data={this.state}*/}
                {/*     options={{*/}
                {/*         title:{*/}
                {/*             display:true,*/}
                {/*             text:'Contribution',*/}
                {/*             fontSize:20*/}
                {/*         },*/}
                {/*         legend:{*/}
                {/*             display:true,*/}
                {/*             position:'right'*/}
                {/*         }*/}
                {/*     }}*/}
                {/*/>*/}
            </div>
        );
    }



    async getContributions()
    {

        // this.state={
        //     labels: Object.keys(this.contributions),
        //     datasets: [
        //         {
        //             label: 'Contributions',
        //             backgroundColor: [
        //                 '#2FDE00',
        //                 '#00A6B4'
        //             ],
        //             data: Object.values(this.contributions)
        //         }
        //     ]
        // }
        this.test={
            labels: ['January', 'February', 'March',
                'April'],
            datasets: [
                {
                    label: 'Rainfall',
                    backgroundColor: [
                        '#2FDE00',
                        '#00A6B4',
                        '#B21F00',
                        '#C9DE00',
                        '#6800B4'
                    ],

                    data: [75, 10, 5, 2]
                }
            ]
        }
    }
}

export default PieChart;
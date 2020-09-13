import React from 'react';
import './App.css';

import {Pie} from 'react-chartjs-2';

class PieChart extends  React.Component<any> {
    public contributions;
    public pieData;
    constructor(props) {
        super(props);
        }


    render() {
        if (this.props.data != null)
        {this.contributions = this.props.data.contributions;
                                  this.getContributions()

                                  return (
                                      <div>
                                          <Pie
                                              data={this.pieData}
                                              options={{
                                                  borderColor: '#000000',
                                                  title: {
                                                      display: true,
                                                      text: 'Contribution (%)',
                                                      fontColor: '##FFFFFF',
                                                      fontSize: 20,
                                                      position: 'top'
                                                  },
                                                  legend: {
                                                      display: true,
                                                      position: 'right'
                                                  }
                                              }}
                                          />
                                      </div>
                                  );
                              }
        return <div></div>;

    }

    getContributions()
    {
        this.pieData={
            labels: Object.keys(this.contributions),
            datasets: [
                {
                    label: 'Contributions',
                    backgroundColor: this.chooseColours(Object.keys(this.contributions).length),
                    data: Object.values(this.contributions)
                }
            ]
        }

    }

    chooseColours(numberOfColours)
    {
        var coloursList=[
            "#2F4F4F","#e70a69","#16bfbf", "#eebe12", "#f0ffff", "#F5F5DC", "#FFE4C4", "#000000", "#FFEBCD", "#0000FF",
            "#8A2BE2", "#A52A2A", "#DEB887", "#5F9EA0", "#7FFF00", "#D2691E", "#FF7F50", "#6495ED", "#FFF8DC",
            "#DC143C", "#00FFFF", "#00008B", "#008B8B", "#B8860B", "#A9A9A9", "#006400","#A9A9A9", "#BDB76B",
            "#8B008B", "#556B2F", "#FF8C00", "#9932CC", "#8B0000", "#E9967A", "#8FBC8F","#483D8B", "#2F4F4F",
            "#2F4F4F", "#00CED1", "#9400D3", "#FF1493", "#00BFFF", "#696969","#696969", "#1E90FF", "#B22222",
            "#FFFAF0", "#228B22", "#FF00FF", "#DCDCDC", "#F8F8FF", "#FFD700", "#DAA520", "#808080", "#008000",
            "#ADFF2F", "#808080", "#F0FFF0", "#FF69B4", "#CD5C5C", "#4B0082", "#FFFFF0","#F0E68C", "#E6E6FA",
            "#FFF0F5", "#7CFC00", "#FFFACD", "#ADD8E6", "#F08080", "#E0FFFF", "#FAFAD2",
            ]

        var colours=coloursList.slice(0,numberOfColours)

        return colours
    }
}

export default PieChart;
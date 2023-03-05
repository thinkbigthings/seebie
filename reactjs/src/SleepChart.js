
import React from 'react';

import {CategoryScale, Chart as ChartJS, Filler, Legend, LinearScale, LineElement, Tooltip, PointElement, Title} from "chart.js";
import { Line } from 'react-chartjs-2';


ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Filler,
    Legend
);


// const options = {
//     responsive: true,
//     scales: {
//         y: {
//             display: true,
//             stacked: true,
//             ticks: {
//                 min: 0, // minimum value
//                 max: 10 // maximum value
//             }
//         }
//     },
//     plugins: {
//         legend: {
//             position: 'top'
//         },
//         title: {
//             display: true,
//             text: 'Sleep Duration',
//         },
//     },
// };


function SleepChart() {

    // TODO update options https://react-chartjs-2.js.org/components/line/

    // TODO pre-emptively handle this situation https://react-chartjs-2.js.org/docs/working-with-datasets

    const labels = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

    const data = {
        labels,
        datasets: [
            {
                fill: true,
                label: 'Hours Asleep',
                data: [6,8,7,7.5,6,8, 7.25],
                borderColor: '#745085',
                backgroundColor:'#595b7c'
            },
        ],
    };

    const options ={
        scales: {
            y: {
                beginAtZero: true
            }
        }
    };

    return (
        <Line options={options} data={data} />
    );
}

export default SleepChart;

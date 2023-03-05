
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

const options = {
    responsive: true,
    // scales: {
    //     yAxes: [{
    //         display: true,
    //         stacked: true,
    //         ticks: {
    //             min: 0, // minimum value
    //             max: 10 // maximum value
    //         }
    //     }]
    // },
    plugins: {
        legend: {
            position: 'top'
        },
        title: {
            display: true,
            text: 'Sleep Duration',
        },
    },
};

const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

export const data = {
    labels,
    datasets: [
        {
            fill: true,
            label: 'Hours Asleep',
            data: [6,8,7,7.5,6,8, 7.25],
            borderColor: 'rgb(53, 162, 235)',
            backgroundColor: 'rgba(53, 162, 235, 0.5)',
        },
    ],
};

function SleepChart() {

    return (
        <Line options={options} data={data} />
    );
}

export default SleepChart;

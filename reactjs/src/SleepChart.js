
import React from 'react';

import {CategoryScale, Chart as ChartJS, Filler, Legend, LinearScale, LineElement, Tooltip, PointElement, Title} from "chart.js";
import { Line } from 'react-chartjs-2';
import DatePicker from "react-datepicker";
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";


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
        <Container>
            <Row>
                <Col>
                    <label htmlFor="dateStart">From</label>
                </Col>
                <Col>
                    <DatePicker
                        className="form-control" id="dateStart" placeholder="Start Date"
                        dateFormat="MMMM d, yyyy"
                        // showTimeSelect
                        // timeIntervals={15}
                        timeCaption="time"
                        timeFormat="p"
                        // selected={data.startTime}
                        // onChange={ date => onChange({startTime : date })}
                    />
                </Col>
                <Col xs={6}></Col>
                <Col>
                    <label htmlFor="dateEnd">To</label>
                </Col>
                <Col>
                    <DatePicker
                        className="form-control" id="dateEnd" placeholder="End Date"
                        dateFormat="MMMM d, yyyy"
                        // showTimeSelect
                        // timeIntervals={15}
                        timeCaption="time"
                        timeFormat="p"
                        // selected={data.stopTime}
                        // onChange={ date => onChange({stopTime : date })}
                    />
                </Col>
            </Row>
            <Line options={options} data={data} />
        </Container>
    );
}

export default SleepChart;

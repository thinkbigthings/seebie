
import React, {useEffect, useState} from 'react';

import {CategoryScale, Chart as ChartJS, Filler, Legend, LinearScale, LineElement, Tooltip, PointElement, Title} from "chart.js";
import { Line } from 'react-chartjs-2';
import DatePicker from "react-datepicker";
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import useCurrentUser from "./useCurrentUser";
import copy from "./Copier";


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


function SleepChart() {

    const {currentUser} = useCurrentUser();

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
        },
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

    let today = new Date();

    let lastMonth = new Date(today.getTime());
    lastMonth.setDate(today.getDate() - 30);
    lastMonth.setHours(0, 0, 0);

    const initialRange = {from: lastMonth, to: today};
    let [range, setRange] = useState(initialRange);

    function updateSearchRange(updateValues) {
        let updatedRange = {...range, ...updateValues};
        setRange(updatedRange);
    }


    let requestParameters = '?'
        + 'from='+encodeURIComponent(SleepDataManager.toIsoString(range.from))
        + '&'
        + 'to='+encodeURIComponent(SleepDataManager.toIsoString(range.to));

    const sleepEndpoint = '/user/'+currentUser.username+'/sleep/chart' + requestParameters;

    const summaryParser = (json) => {
        let parsed = {};
        parsed.stopTime = new Date(Date.parse(json.stopTime));
        parsed.hours = json.durationMinutes / 60;
        return parsed;
    }

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(jsonArray => jsonArray.map(json => summaryParser(json)))
            .then(console.log)
            // .then(setChartData)
            // .then(() => setLoaded(true))
    }, [setRange, sleepEndpoint]);

    return (
        <Container>
            <Row>
                <Col xs={6}></Col>

                <Col xs={1}>
                    <label htmlFor="dateStart">From</label>
                </Col>
                <Col xs={2}>
                    <DatePicker
                        className="form-control" id="dateStart" placeholder="Start Date"
                        dateFormat="MMMM d, yyyy"
                        selected={range.from}
                        onChange={ date => updateSearchRange({from : date })}
                    />
                </Col>
                <Col xs={1}>
                    <label htmlFor="dateEnd">To</label>
                </Col>
                <Col xs={2}>
                    <DatePicker
                        className="form-control" id="dateEnd" placeholder="End Date"
                        dateFormat="MMMM d, yyyy"
                        selected={range.to}
                        onChange={ date => updateSearchRange({to : date })}
                    />
                </Col>
            </Row>
            <Line options={options} data={data} />
        </Container>
    );
}

export default SleepChart;

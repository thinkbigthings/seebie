
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

    const initialChartData = {
        labels: ['from', 'to'],
        datasets: [{
            fill: true,
            label: 'Hours Asleep',
            data: [8, 8],
            borderColor: '#745085',
            backgroundColor:'#595b7c'
        }]
    };

    let [chartData, setChartData] = useState(initialChartData);

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

    const updateChart = (fetchedSleepData) => {

        // TODO server could return responses that are easier to process: separate arrays for x and y

        // TODO copy chart data first

        let x = [];
        let y = [];
        for(let i=0 ; i< fetchedSleepData.length; i++) {
            x[i] = fetchedSleepData[i].stopTime;
            y[i] = fetchedSleepData[i].hours;
        }
        chartData.labels = x;
        chartData.datasets[0].data = y;

        setChartData(chartData);
    }

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(jsonArray => jsonArray.map(json => summaryParser(json)))
            .then(console.log)
            // .then(parsed => updateChart(parsed))
            // .then(() => setLoaded(true))
    }, [range, sleepEndpoint]);

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
            <Line datasetIdKey="id" options={options} data={chartData} />
        </Container>
    );
}

export default SleepChart;


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
        datasets: [{
            fill: true,
            label: 'Hours Asleep',
            data: [{x: '2023-01-01', y: 8}, {x: '2023-01-02', y: 8}],
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

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newChartData = copy(chartData);
                newChartData.datasets[0].data = json;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, setChartData]);

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

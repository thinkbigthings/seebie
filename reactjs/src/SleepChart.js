
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

const createInitialRange = () => {

    let today = new Date();
    today.setHours(23, 59, 59);

    let lastMonth = new Date(today.getTime());
    lastMonth.setDate(today.getDate() - 30);
    lastMonth.setHours(0, 0, 0);

    return {from: lastMonth, to: today};
}

function SleepChart() {

    const {currentUser} = useCurrentUser();

    const options ={
        scales: {
            y: {
                beginAtZero: true,
                title: {
                    text: 'Hours Asleep',
                    display: true
                }
            }

        },
        plugins: {
            legend: {
                display: false,
                position: 'top'
            },
            // title: {
            //     display: true,
            //     text: 'Hours Asleep Chart',
            // },
        },
    };


    let [range, setRange] = useState(createInitialRange());

    const initialChartData = {
        datasets: [{
            fill: true,
            data: [],
            borderColor: '#745085',
            backgroundColor:'#595b7c'
        }]
    };

    let [chartData, setChartData] = useState(initialChartData);

    // If not at least two data points then UI can display "NO DATA"
    const hasData = chartData.datasets[0].data.length > 1;

    const isDateRangeValid = (d1, d2)  => {
        let j1 = d1.toJSON().slice(0, 10);
        let j2 = d2.toJSON().slice(0, 10);
        return j1 < j2;
    }

    function updateSearchRange(updateValues) {
        let updatedRange = {...range, ...updateValues};
        if( isDateRangeValid(updatedRange.from, updatedRange.to) ) {
            setRange(updatedRange);
        }
    }

    let requestParameters = '?'
        + 'from='+encodeURIComponent(SleepDataManager.toIsoString(range.from)) + '&'
        + 'to='+encodeURIComponent(SleepDataManager.toIsoString(range.to));

    console.log("Current request parameters: " + requestParameters);

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
            <Row className="pt-3 pb-6">
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
            { hasData
                ?   <Line className="pt-3" datasetIdKey="id" options={options} data={chartData} />
                :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>
            }

        </Container>
    );
}

export default SleepChart;


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


const chartOptions ={
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

const initialChartData = {
    datasets: [{
        fill: true,
        data: [],
        borderColor: '#745085',
        backgroundColor:'#595b7c'
    }]
};

const isDateRangeValid = (d1, d2)  => {
    let j1 = d1.toJSON().slice(0, 10);
    let j2 = d2.toJSON().slice(0, 10);
    return j1 < j2;
}

const createInitialRange = () => {

    let today = new Date();
    today.setHours(23, 59, 59);

    let lastMonth = new Date(today.getTime());
    lastMonth.setDate(today.getDate() - 30);
    lastMonth.setHours(0, 0, 0);

    return {from: lastMonth, to: today};
}

function SleepChart(props) {

    const {reloadCount} = props;

    const {currentUser} = useCurrentUser();

    let [range, setRange] = useState(createInitialRange());
    let [chartData, setChartData] = useState(initialChartData);

    function updateSearchRange(updateValues) {
        let updatedRange = {...range, ...updateValues};
        if( isDateRangeValid(updatedRange.from, updatedRange.to) ) {
            setRange(updatedRange);
        }
    }

    let requestParameters = '?'
        + 'from='+encodeURIComponent(SleepDataManager.toIsoString(range.from)) + '&'
        + 'to='+encodeURIComponent(SleepDataManager.toIsoString(range.to));

    const sleepEndpoint = '/user/'+currentUser.username+'/sleep/chart' + requestParameters;

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newChartData = copy(initialChartData);
                newChartData.datasets[0].data = json;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, reloadCount]);

    return (
        <Container>
            <Row className="pt-3 pb-6">
                <Col xs={4}></Col>

                <Col xs={1}>
                    <label htmlFor="dateStart">From</label>
                </Col>
                <Col xs={3}>
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
                <Col xs={3}>
                    <DatePicker
                        className="form-control" id="dateEnd" placeholder="End Date"
                        dateFormat="MMMM d, yyyy"
                        selected={range.to}
                        onChange={ date => updateSearchRange({to : date })}
                    />
                </Col>
            </Row>
            { chartData.datasets[0].data.length > 1
                ?   <Line className="pt-3" datasetIdKey="sleepChart" options={chartOptions} data={chartData} />
                :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>
            }

        </Container>
    );
}

export default SleepChart;

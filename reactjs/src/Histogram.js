
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'


import {Bar} from 'react-chartjs-2';
import DatePicker from "react-datepicker";
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import useCurrentUser from "./useCurrentUser";
import copy from "./Copier";
import {NavHeader} from "./App";

Chart.register(...registerables)

const histOptions = {
    scales: {
        x: {
            offset: true,
            grid: {
                offset: true
            }
        }
    }
}

const initialChartData = {
    datasets: [{
        fill: true,
        data: [],
        label: 'Hours Asleep',
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

function Histogram(props) {

    const {createdCount} = props;

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

    function histInt(arr) {
        const histogram = arr.reduce((histogram, value) => {
            histogram[value] = histogram.hasOwnProperty(value) ? histogram[value] + 1 : 1;
            return histogram;
        }, {});

        // Extract the keys (bins) and values (counts) from the histogram
        const bins = Object.keys(histogram).map(Number);
        const counts = Object.values(histogram);

        // Return the data in the desired format
        return {
            'labels': bins,
            'data': counts
        };
    }

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newChartData = copy(initialChartData);
                let histData = histInt(json.map(e=>e.y).map(e=>Math.floor(e)))
                newChartData.labels = histData.labels;
                newChartData.datasets[0].data = histData.data;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, createdCount]);

    const chartArea = chartData.datasets[0].data.length > 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={chartData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    return (
        <Container>
            <NavHeader title="Sleep Chart" />

            <Row className="pb-3">
                <Col className="col-2" >
                    <label className="d-inline-block" htmlFor="dateStart">From</label>
                </Col>
                <Col className="col-md-4">
                    <DatePicker
                        className="form-control d-inline-block" id="dateStart" dateFormat="MMMM d, yyyy"
                        onChange={ date => updateSearchRange({from : date })}
                        selected={range.from}
                    />
                </Col>
            </Row>
            <Row className={"pb-3"}>
                <Col className="col-2">
                    <label htmlFor="dateEnd">To</label>
                </Col>
                <Col className="col-md-4">
                    <DatePicker
                        className="form-control" id="dateEnd" dateFormat="MMMM d, yyyy"
                        onChange={ date => updateSearchRange({to : date })}
                        selected={range.to}
                    />
                </Col>
            </Row>

            {chartArea}

        </Container>
    );
}

export default Histogram;

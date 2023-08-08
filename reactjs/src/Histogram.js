
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'


import {Bar} from 'react-chartjs-2';
import DateRangePicker from "./component/DateRangePicker";
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import useCurrentUser from "./useCurrentUser";
import copy from "./Copier";
import {NavHeader} from "./App";
import {Collapse} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDown} from "@fortawesome/free-solid-svg-icons";

Chart.register(...registerables)

const histOptions = {
    scales: {
        x: {
            offset: true,
            grid: {
                offset: true
            },
            ticks: {
                color: 'white',
                font: {
                    size: 18,
                }
            }
        },
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

    const binSizeOptions = [
        {value: 1, text: '60 minutes'},
        {value: 2, text: '30 minutes'},
        {value: 4, text: '15 minutes'},
    ];

    const [binHrParts, setBinHrParts] = useState(binSizeOptions[0].value);

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

    const handlePartChange = event => {
        setBinHrParts(event.target.value);
    };

    useEffect(() => {

        function roundToNearestPart(num, numParts) {
            return Math.round(num * numParts) / numParts;
        }

        function roundToTwoDecimals(num) {
            return Math.round((num + Number.EPSILON) * 100) / 100;
        }
        const createHistogram = (arr) => {
            const histogram = arr.reduce((hist, value) => {
                hist[value] = hist.hasOwnProperty(value) ? hist[value] + 1 : 1;
                return hist;
            }, {});

            // Get the minimum and maximum value of the array
            const minVal = Math.min(...arr);
            const maxVal = Math.max(...arr);

            // Include "empty" bins
            for (let i = minVal; i <= maxVal; i += 1/binHrParts) {
                if (!histogram.hasOwnProperty(i)) {
                    histogram[i] = 0;
                }
            }

            // Extract the keys (bins) and values (counts) from the histogram
            const bins = Object.keys(histogram).map(Number).sort((a, b) => a - b);
            const counts = bins.map(bin => histogram[bin]);

            // Return the data in the desired format
            return {
                'labels': bins,
                'data': counts
            };
        }
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newChartData = copy(initialChartData);
                let newData = json.map(e=>e.y)
                                    .map(e=>roundToNearestPart(e, binHrParts))
                                    .map(e=>roundToTwoDecimals(e));
                let histData = createHistogram(newData);
                newChartData.labels = histData.labels;
                newChartData.datasets[0].data = histData.data;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, createdCount, binHrParts]);

    const chartArea = chartData.datasets[0].data.length > 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={chartData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    const [collapsed, setCollapsed] = useState(true);
    const filterTitle = "Select Sleep Data";
    const collapseIconRotation = collapsed ? "" : "fa-rotate-180";

    return (
        <Container>
            <NavHeader title="Sleep Hours Histogram"/>

            <Row className={"pb-3"}>
                <Col className="col-2">
                    <label htmlFor="dateEnd">Bin Size</label>
                </Col>
                <Col className="col-md-4">
                    <Form.Select value={binHrParts} onChange={handlePartChange}>
                        {
                            binSizeOptions.map(option => {
                                return (
                                    <option key={option.value} value={option.value}>
                                        {option.text}
                                    </option>
                                )
                            })
                        }
                    </Form.Select>
                </Col>
            </Row>
            <Row className={"pb-3"}>
                <Col className="col-12">

                    <Button
                        variant="dark"
                        className={"w-100 text-start border border-light-subtle"}
                        onClick={() => setCollapsed(!collapsed)}
                        aria-controls="example-collapse-text"
                        aria-expanded={!collapsed}
                    >
                        {filterTitle}
                        <FontAwesomeIcon className={"me-2 mt-1 float-end " + collapseIconRotation} icon={faAngleDown} ></FontAwesomeIcon>

                    </Button>
                </Col>
            </Row>
            <Row className={"pb-3"}>
                <Col className="col-12">
                    <Collapse in={!collapsed}>

                        <DateRangePicker selectStartDate={range.from}
                                         onStartSelection={date => updateSearchRange({from: date})}
                                         selectEndDate={range.to}
                                         onEndSelection={date => updateSearchRange({to: date})} />

                    </Collapse>
                </Col>
            </Row>

            {chartArea}

        </Container>
    );
}

export default Histogram;

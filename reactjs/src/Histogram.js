
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'

import {Bar} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import useCurrentUser from "./useCurrentUser";
import {NavHeader} from "./App";
import CollapsibleFilter from "./component/CollapsibleFilter";

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

const createInitialChartData = (title, bgColor) => {
    return {
        datasets: [{
            fill: true,
            data: [],
            label: title,
            borderColor: '#745085',
            backgroundColor: bgColor
        }]
    };
}

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

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    const {createdCount} = props;

    const {currentUser} = useCurrentUser();

    const binSizeOptions = [
        {value: 1, text: '60 minutes'},
        {value: 2, text: '30 minutes'},
        {value: 4, text: '15 minutes'},
    ];

    const [binHrParts, setBinHrParts] = useState(binSizeOptions[0].value);

    const chart1Constants = {
        title: "Set 1",
        bgColor: '#595b7c'
    };

    let [range, setRange] = useState(createInitialRange());
    let [chartData, setChartData] = useState(createInitialChartData(chart1Constants.title, chart1Constants.bgColor));

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

        const newChartData = {
            datasets: [{
                fill: true,
                data: [],
                label: chart1Constants.title,
                borderColor: '#745085',
                backgroundColor: '#595b7c'
            }]
        };

        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(json => {
                let newData = json.map(e=>e.y)
                                    .map(e=>roundToNearestPart(e, binHrParts))
                                    .map(e=>roundToTwoDecimals(e));
                let histData = createHistogram(newData);
                newChartData.labels = histData.labels;
                newChartData.datasets[0].data = histData.data;
                setChartData(newChartData);
            })
    }, [sleepEndpoint, createdCount, binHrParts, chart1Constants.title]);

    const chartArea = chartData.datasets[0].data.length > 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={chartData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    const [collapsed, setCollapsed] = useState(true);
    const filterTitle = chart1Constants.title;

    let onChangeStart = date => updateSearchRange({from: date});
    let onChangeEnd = date => updateSearchRange({to: date});
    let selectedStart = range.from;
    let selectedEnd = range.to;

    return (
        <Container>
            <NavHeader title="Sleep Hours Histogram"/>

            <Row className={"pb-3"}>
                <Col className="col-2">
                    <label>Bin Size</label>
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

                    <CollapsibleFilter selectedStart={selectedStart}
                                       onChangeStart={onChangeStart}
                                       selectedEnd={selectedEnd}
                                       onChangeEnd={onChangeEnd}
                                       title={filterTitle}
                                       collapsed={collapsed}
                                       onCollapseClick={() => setCollapsed(!collapsed)} />

                </Col>
            </Row>

            {chartArea}

        </Container>
    );
}

export default Histogram;

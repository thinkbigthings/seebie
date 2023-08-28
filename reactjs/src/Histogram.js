
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'

import {Bar} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import SleepDataManager from "./SleepDataManager";
import useCurrentUser from "./useCurrentUser";
import {NavHeader} from "./App";
import CollapsibleFilter from "./component/CollapsibleFilter";
import {basicHeader} from "./BasicHeaders";

Chart.register(...registerables)

const histOptions = {
    scales: {
        x: {
            stacked: true,
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
        y: {
            stacked: true
        }
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

const requestHeaders = basicHeader();

const fetchPost = (url, body) => {

    const bodyString = typeof body === 'string' ? body : JSON.stringify(body);

    const requestMeta = {
        headers: requestHeaders,
        method: 'POST',
        body: bodyString
    };

    return fetch(url, requestMeta);
}

// the resulting object goes into the chartData datasets array
const createDataset = (title, bgColor, data) => {
    return {
            fill: true,
            data: data,
            label: title,
            borderColor: bgColor,
            backgroundColor: bgColor
        };
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
        {value: 60, text: '60 minutes'},
        {value: 30, text: '30 minutes'},
        {value: 15, text: '15 minutes'},
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

    const sleepEndpoint = '/user/'+currentUser.username+'/sleep/histogram';

    const handlePartChange = event => {
        setBinHrParts(event.target.value);
    };


    useEffect(() => {

        const color = ['#595b7c', '#484a6b'];

        const histogramRequest = {
            binSize: binHrParts,
            filters: {
                dataFilters: [
                    {
                        from: SleepDataManager.toIsoString(range.from),
                        to:   SleepDataManager.toIsoString(range.to)
                    }
                ]
            }
        }

        fetchPost(sleepEndpoint, histogramRequest)
            .then(response => response.json())
            .then(histData => {

                let labels = histData.bins.map(bin => bin/60);
                let stacked = histData.dataSets.map((data, i) => createDataset("Set " + i, color[i], data));
                // stacked[1] = createDataset("Set " + 1, color[1], stacked[0].data); // for testing
                let newChartData = {
                    labels: labels,
                    datasets: stacked
                };

                setChartData(newChartData);
            })
    }, [sleepEndpoint, createdCount, binHrParts, chart1Constants.title, range]);

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

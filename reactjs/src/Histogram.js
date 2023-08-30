
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
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import copy from "./Copier";

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

const histogramColor = ['#595b7c', '#484a6b'];

const binSizeOptions = [
    {value: 60, text: '60 minutes'},
    {value: 30, text: '30 minutes'},
    {value: 15, text: '15 minutes'},
];

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
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const {currentUser} = useCurrentUser();
    const sleepEndpoint = '/user/' + currentUser.username + '/sleep/histogram';


    let [pageState, setPageState] = useState({
        binSize: 60,
        range: createInitialRange(),
        filterTitle: "Set 1",
        bgColor: '#595b7c',
        barData: {
            labels: [],
            datasets: []
        }
    });


    let [chartData, setChartData] = useState({
        datasets: [createDataset("Set 1", '#595b7c', [])]
    });


    // this is in the useEffect dependency array, so a user changing a setting will trigger a call to the server
    // when that call returns, the pageDisplay state is updated, which triggers re-render,
    // so pageDisplay should NOT be in the dependency array for useEffect, or it will cause an infinite loop
    let samplePageSettings = {
        binSize: 30,
        filter: [
                {
                    from: "range.from", // date object
                    to:   "range.to" // date object
                }
         ]
     }

    let samplePageDisplay = {
        filter: [
            {
                title: "Set 1", // inferred by page settings
                collapsed: true, // managed by the containing component so that it can re-render and its child components re-render
                selectedStart: "range.from", // date object, set by page settings
                selectedEnd: "range.to" // date object, set by page settings
            }
        ],
        barData: { // this is assigned directly to the field like so <Bar data={pageDisplay.barData} />
            labels: [], // set by returned data
            datasets: [
                {
                    fill: true,
                    data: [], // set by returned data
                    label: "Set 1", // inferred by page settings
                    borderColor: '#595b7c', // set by current index and known values
                    backgroundColor: '#595b7c' // set by page settings and known values
                }
            ]
        }
    }


    const onAddFilter = () => {

    }


    function updateSearchRange(updateValues) {
        let newPageState = copy(pageState);
        let updatedRange = {...pageState.range, ...updateValues};
        if( isDateRangeValid(updatedRange.from, updatedRange.to) ) {
            newPageState.range = updatedRange;
            setPageState(newPageState);
        }
    }


    const handleBinSizeChange = event => {

        // copy method is not working, the date types are copied as strings?
        // let newPageState = copy(pageState);

        let newPageState = structuredClone(pageState);
        // console.log(newPageState);
        // Object.assign doesn't do a deep copy, be careful
        // let newPageState = Object.assign({}, pageState);

        // this copies the number as a string, but it seems to still work?
        newPageState.binSize = event.target.value;

        setPageState(newPageState);
    };


    useEffect(() => {

        const histogramRequest = {
            binSize: pageState.binSize,
            filters: {
                dataFilters: [
                    {
                        from: SleepDataManager.toIsoString(pageState.range.from),
                        to:   SleepDataManager.toIsoString(pageState.range.to)
                    }
                ]
            }
        }

        fetchPost(sleepEndpoint, histogramRequest)
            .then(response => response.json())
            .then(histData => {

                let labels = histData.bins.map(bin => bin/60);
                let stacked = histData.dataSets.map((data, i) => createDataset(pageState.filterTitle, pageState.bgColor, data));

                let newPageState = structuredClone(pageState);
                newPageState.barData.labels = labels;
                newPageState.barData.datasets = stacked;
                // setPageState(newPageState);
                setChartData({
                    labels: labels,
                    datasets: stacked
                });
            })
    }, [sleepEndpoint, createdCount, pageState]);

    // TODO check all datasets
    const chartArea = chartData.datasets[0].data.length > 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={chartData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    const [collapsed, setCollapsed] = useState(true);

    let onChangeStart = date => updateSearchRange({from: date});
    let onChangeEnd = date => updateSearchRange({to: date});
    let selectedStart = pageState.range.from;
    let selectedEnd =pageState.range.to;


    return (
        <Container>
            <NavHeader title="Sleep Histogram">
                <Button variant="secondary" onClick={ onAddFilter } >
                    <FontAwesomeIcon className="me-2" icon={faPlus} />
                    Add
                </Button>

            </NavHeader>
            {/*{data.content.map(user =>*/}
            {/*    <tr key={user.username}>*/}
            {/*        <td>*/}
            {/*            <Link to={"/users/" + user.username + "/edit" } >*/}
            {/*                {user.displayName}*/}
            {/*            </Link>*/}
            {/*        </td>*/}
            {/*    </tr>*/}
            {/*)}*/}
            <Row>
                <Col className="col-12">

                    <CollapsibleFilter selectedStart={selectedStart}
                                       onChangeStart={onChangeStart}
                                       selectedEnd={selectedEnd}
                                       onChangeEnd={onChangeEnd}
                                       title={pageState.filterTitle}
                                       collapsed={collapsed}
                                       onCollapseClick={() => setCollapsed(!collapsed)} />

                </Col>
            </Row>

            {chartArea}

            <Row className={"pt-3"}>
                <Col className="col-4">
                    <label>Bin Size</label>
                </Col>
                <Col className="col-md-4">
                    <Form.Select value={pageState.binSize} onChange={handleBinSizeChange}>
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

        </Container>
    );
}

export default Histogram;

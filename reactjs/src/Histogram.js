
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
import {faPlus, faRemove} from "@fortawesome/free-solid-svg-icons";

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

const pageSettingsToRequest = (pageSettings) => {

    const newDataFilters = pageSettings.filters.map((filter) => { return {
            from: SleepDataManager.toIsoString(filter.from),
            to: SleepDataManager.toIsoString(filter.to)
        }}
    );

    return {
        binSize: pageSettings.binSize,
        filters: {
            dataFilters: newDataFilters
        }
    }

}

const createInitialRange = () => {

    let today = new Date();
    today.setHours(23, 59, 59);

    let lastMonth = new Date(today.getTime());
    lastMonth.setDate(today.getDate() - 30);
    lastMonth.setHours(0, 0, 0);

    return {from: lastMonth, to: today};
}

const initialRange = createInitialRange();

function Histogram(props) {

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const {currentUser} = useCurrentUser();
    const sleepEndpoint = '/user/' + currentUser.username + '/sleep/histogram';

    let [pageSettings, setPageSettings] = useState({
                                                                    binSize: 60,
                                                                    filters: [
                                                                        initialRange
                                                                    ]
                                                                });

    let [filterDisplay, setFilterDisplay] = useState([ {
                                                                        title: "Set 1",
                                                                        collapsed: true
                                                                    }
                                                                ]);

    let[barData, setBarData] = useState({
                                                                    labels: [],
                                                                    datasets: []
                                                                });

    const onAddFilter = () => {

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.push(initialRange);
        setPageSettings(newPageSettings);

        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay.push({
            title: "Set " + (newFilterDisplay.length + 1),
            collapsed: false
        });
        setFilterDisplay(newFilterDisplay);
    }

    function onRemoveFilter(i) {

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters = newPageSettings.filters.splice(i, 1);
        setPageSettings(newPageSettings);

        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay = newFilterDisplay.splice(i, 1);
        setFilterDisplay(newFilterDisplay);
    }

    function onToggleCollapse(i) {
        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay[i].collapsed = ! newFilterDisplay[i].collapsed;
        setFilterDisplay(newFilterDisplay);
    }

    function updateSearchRange(updateValues, i) {
        let newPageSettings = structuredClone(pageSettings);
        let updatedRange = {...pageSettings.filters[i], ...updateValues};
        if( isDateRangeValid(updatedRange.from, updatedRange.to) ) {
            newPageSettings.filters[i] = updatedRange;
            setPageSettings(newPageSettings);
        }
    }

    const updateBinSize = event => {
        let newPageState = structuredClone(pageSettings);
        newPageState.binSize = event.target.value;
        setPageSettings(newPageState);
    };


    useEffect(() => {
        fetchPost(sleepEndpoint, pageSettingsToRequest(pageSettings))
            .then(response => response.json())
            .then(histData => {
                setBarData({
                    labels: histData.bins.map(bin => bin/60),
                    datasets: histData.dataSets.map((data, i) => createDataset("Set " + (i+1), histogramColor[i], data))
                });
            })
    }, [sleepEndpoint, createdCount, pageSettings]);

    // TODO check all datasets
    const chartArea = <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={barData} />

    // const chartArea = filterDisplay.barData.datasets[0].data.length > 1
    //     ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={filterDisplay.barData} />
    //     :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>


    return (
        <Container>
            <NavHeader title="Sleep Histogram">
                <Button variant="secondary" onClick={ onAddFilter } >
                    <FontAwesomeIcon className="me-2" icon={faPlus} />
                    Add
                </Button>
            </NavHeader>

            {
                pageSettings.filters.map((filter, i) => {
                    return (
                        <Row key={i}>
                            <Col className="col-10 px-0">

                                <CollapsibleFilter selectedStart={filter.from}
                                                   onChangeStart={(date) => updateSearchRange({from:date}, i)}
                                                   selectedEnd={filter.to}
                                                   onChangeEnd={(date) => updateSearchRange({to:date}, i)}
                                                   title={filterDisplay[i].title}
                                                   collapsed={filterDisplay[i].collapsed}
                                                   onCollapseClick={() => onToggleCollapse(i)} />

                            </Col>
                            <Col className={"col-2 px-0"}>
                                <Button variant="warning" onClick={ () => onRemoveFilter(i) } >
                                    <FontAwesomeIcon icon={faRemove} />
                                </Button>
                            </Col>
                        </Row>
                    )
                })
            }

            {chartArea}

            <Row className={"pt-3"}>
                <Col className="col-4">
                    <label>Bin Size</label>
                </Col>
                <Col className="col-md-4">
                    <Form.Select value={pageSettings.binSize} onChange={updateBinSize}>
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

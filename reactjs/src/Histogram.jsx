
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'

import {Bar} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import SleepDataManager from "./SleepDataManager";
import {NavHeader} from "./App";
import CollapsibleFilter from "./component/CollapsibleFilter";
import {basicHeader} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus, faRemove} from "@fortawesome/free-solid-svg-icons";
import {createInitialRange} from "./SleepChart";
import {useParams} from "react-router-dom";

Chart.register(...registerables)

const histOptions = {
    scales: {
        x: {
            stacked: false,
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
            stacked: false
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

const histogramColor = ['#897b9c', '#596b7c', '#393b4c'];

const binSizeOptions = [
    {value: 60, text: '60 minutes'},
    {value: 30, text: '30 minutes'},
    {value: 15, text: '15 minutes'},
];

// the resulting object goes into the chartData datasets array
const createDataset = (displayInfo, data) => {
    return {
            fill: true,
            data: data,
            label: displayInfo.title,
            borderColor: displayInfo.color,
            backgroundColor: displayInfo.color
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

const initialRange = createInitialRange();

function Histogram(props) {

    const {username} = useParams();

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const sleepEndpoint = '/api/user/' + username + '/sleep/histogram';

    let [numFiltersCreated, setNumFiltersCreated] = useState(1);

    let [pageSettings, setPageSettings] = useState({
                                                                    binSize: 60,
                                                                    filters: [
                                                                        {
                                                                            from: initialRange.from,
                                                                            to: initialRange.to,
                                                                            title: "Set " + numFiltersCreated,
                                                                            color: histogramColor[0]
                                                                        }
                                                                    ]
                                                                });

    let [filterDisplay, setFilterDisplay] = useState([ {
                                                                        collapsed: true
                                                                    }
                                                                ]);

    let[barData, setBarData] = useState({
                                                                    labels: [],
                                                                    datasets: []
                                                                });

    const onAddFilter = () => {

        let newNumFiltersCreated = numFiltersCreated + 1;

        let usedColors = pageSettings.filters.map(filter => filter.color);
        let availableColors = histogramColor.filter(color => ! usedColors.includes(color));

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.push({
            from: initialRange.from,
            to: initialRange.to,
            title: "Set " + newNumFiltersCreated,
            color: availableColors[0]
        });
        setPageSettings(newPageSettings);

        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay.push({
            collapsed: false
        });
        setFilterDisplay(newFilterDisplay);

        setNumFiltersCreated(newNumFiltersCreated);
    }

    function onRemoveFilter(i) {

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.splice(i, 1);
        setPageSettings(newPageSettings);

        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay.splice(i, 1);
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
                    datasets: histData.dataSets.map((data, i) => createDataset(pageSettings.filters[i], data))
                });
            })
    }, [sleepEndpoint, createdCount, pageSettings]);

    const chartArea = barData.datasets.filter(dataset => dataset.data.length > 0).length >= 1
        ?    <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={barData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    return (
        <Container>
            <NavHeader title="Sleep Histogram">
                <Button variant="secondary" disabled={pageSettings.filters.length === 3} onClick={ onAddFilter } >
                    <FontAwesomeIcon icon={faPlus} />
                </Button>
            </NavHeader>

            {
                pageSettings.filters.map((filter, i) => {
                    return (
                        <Row key={i}>
                            <Col className="col-10 px-1">

                                <CollapsibleFilter selectedStart={filter.from}
                                                   color={filter.color}
                                                   onChangeStart={(date) => updateSearchRange({from:date}, i)}
                                                   selectedEnd={filter.to}
                                                   onChangeEnd={(date) => updateSearchRange({to:date}, i)}
                                                   title={pageSettings.filters[i].title}
                                                   collapsed={filterDisplay[i].collapsed}
                                                   onCollapseClick={() => onToggleCollapse(i)} />

                            </Col>
                            <Col className={"px-0"}>
                                <Button variant="secondary" className="mx-1" disabled={pageSettings.filters.length === 1} onClick={ () => onRemoveFilter(i) } >
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

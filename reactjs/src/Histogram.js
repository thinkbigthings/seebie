
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

const initialRange = createInitialRange();

function Histogram(props) {

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const {currentUser} = useCurrentUser();
    const sleepEndpoint = '/user/' + currentUser.username + '/sleep/histogram';


    // this is in the useEffect dependency array, so a user changing a setting will trigger a call to the server
    // when that call returns, the filterDisplay state is updated, which triggers re-render,
    // so filterDisplay should NOT be in the dependency array for useEffect, or it will cause an infinite loop
    let [pageSettings, setPageSettings] = useState({
                                                                    binSize: 60,
                                                                    filters: [
                                                                        {
                                                                            from: initialRange.from,
                                                                            to: initialRange.to
                                                                        }
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
        console.log("Add filter");
    }

    function onToggleCollapse() {
        let newFilterDisplay = structuredClone(filterDisplay);
        newFilterDisplay[0].collapsed = ! newFilterDisplay[0].collapsed;
        setFilterDisplay(newFilterDisplay);
    }

    function updateSearchRange(updateValues) {
        let newPageSettings = structuredClone(pageSettings);
        let updatedRange = {...pageSettings.filters[0], ...updateValues};
        if( isDateRangeValid(updatedRange.from, updatedRange.to) ) {
            newPageSettings.filters[0] = updatedRange;
            setPageSettings(newPageSettings);
        }
    }

    const handleBinSizeChange = event => {
        let newPageState = structuredClone(pageSettings);
        newPageState.binSize = event.target.value;
        setPageSettings(newPageState);
    };


    useEffect(() => {

        const histogramRequest = {
            binSize: pageSettings.binSize,
            filters: {
                dataFilters: [
                    {
                        from: SleepDataManager.toIsoString(pageSettings.filters[0].from),
                        to:   SleepDataManager.toIsoString(pageSettings.filters[0].to)
                    }
                ]
            }
        }

        fetchPost(sleepEndpoint, histogramRequest)
            .then(response => response.json())
            .then(histData => {

                // TODO title is inferred from the dates or index of the dataset

                let labels = histData.bins.map(bin => bin/60);
                let dataSets = histData.dataSets.map((data, i) => createDataset("Set " + (i+1), histogramColor[0], data));

                setBarData({
                    labels: labels,
                    datasets: dataSets
                });
            })
    }, [sleepEndpoint, createdCount, pageSettings]);

    // TODO check all datasets
    const chartArea = <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={barData} />

    // const chartArea = filterDisplay.barData.datasets[0].data.length > 1
    //     ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={filterDisplay.barData} />
    //     :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    let onChangeStart = date => updateSearchRange({from: date});
    let onChangeEnd = date => updateSearchRange({to: date});

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

                    <CollapsibleFilter selectedStart={pageSettings.filters[0].from}
                                       onChangeStart={onChangeStart}
                                       selectedEnd={pageSettings.filters[0].to}
                                       onChangeEnd={onChangeEnd}
                                       title={filterDisplay[0].title}
                                       collapsed={filterDisplay[0].collapsed}
                                       onCollapseClick={onToggleCollapse} />

                </Col>
            </Row>

            {chartArea}

            <Row className={"pt-3"}>
                <Col className="col-4">
                    <label>Bin Size</label>
                </Col>
                <Col className="col-md-4">
                    <Form.Select value={pageSettings.binSize} onChange={handleBinSizeChange}>
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

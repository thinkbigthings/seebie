
import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'

import {Bar} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import SleepDataManager from "./SleepDataManager";
import {NavHeader} from "./App";
import {fetchPost, GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus, faRemove} from "@fortawesome/free-solid-svg-icons";
import {createRange} from "./SleepChart";
import {useParams} from "react-router-dom";
import Modal from "react-bootstrap/Modal";

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
            label: toShortName(displayInfo.challenge.name),
            borderColor: displayInfo.color,
            backgroundColor: displayInfo.color
        };
}

const pageSettingsToRequest = (pageSettings) => {

    const newDataFilters = pageSettings.filters.map((filter) => { return {
            from: SleepDataManager.toIsoString(filter.challenge.start),
            to: SleepDataManager.toIsoString(filter.challenge.finish)
        }}
    );

    return {
        binSize: pageSettings.binSize,
        filters: {
            dataFilters: newDataFilters
        }
    }

}

const toShortName = (name) => {
    return name.substring(0,27);
}

const toExactTimes = (challengeList) => {
    let exactChallengeList = {};
    exactChallengeList.current = challengeList.current === null ? null : toExactTime(challengeList.current);
    exactChallengeList.upcoming = challengeList.upcoming.map(toExactTime);
    exactChallengeList.completed = challengeList.completed.map(toExactTime);
    return exactChallengeList;
}

const toExactTime = (challenge) => {
    let start = new Date(challenge.start);
    let finish = new Date(challenge.finish);
    start.setHours(0, 0, 0);
    finish.setHours(23, 59, 59);
    return {
        name: challenge.name,
        description: challenge.description,
        start: start,
        finish: finish
    }
}

const last30days = createRange(30);

const defaultChallenge = {
    name: "Last 30 Days",
    description: "Last 30 Days",
    start: last30days.from,
    finish: last30days.to
}

function Histogram2(props) {

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const {username} = useParams();

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;
    const histogramEndpoint = `/api/user/${username}/sleep/histogram`;

    let [pageSettings, setPageSettings] = useState({
                                                                    binSize: 60,
                                                                    filters: [
                                                                        {
                                                                            challenge: defaultChallenge,
                                                                            color: histogramColor[0]
                                                                        }
                                                                    ]
                                                                });

    const [savedChallenges, setSavedChallenges] = useState({
        current: null,
        upcoming: [],
        completed: []
    });
    const [showSelectChallenge, setShowSelectChallenge] = useState(false);

    let[barData, setBarData] = useState({
                                                                    labels: [],
                                                                    datasets: []
                                                                });

    const onSelectChallenge = event => {

        let usedColors = pageSettings.filters.map(filter => filter.color);
        let availableColors = histogramColor.filter(color => ! usedColors.includes(color));

        let newPageSettings = structuredClone(pageSettings);

        // event target value is the challenge name, option value has to be a string not an object, so need to find it
        savedChallenges.completed.filter(challenge => challenge.name === event.target.value)
            .forEach(matchedChallenge => {
                newPageSettings.filters.push({
                    challenge: matchedChallenge,
                    color: availableColors[0]
                });
        });

        setShowSelectChallenge(false);
        setPageSettings(newPageSettings);
    }

    function onRemoveFilter(i) {
        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.splice(i, 1);
        setPageSettings(newPageSettings);
    }

    const updateBinSize = event => {
        let newPageState = structuredClone(pageSettings);
        newPageState.binSize = event.target.value;
        setPageSettings(newPageState);
    };

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(toExactTimes)
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, []);

    useEffect(() => {
        fetchPost(histogramEndpoint, pageSettingsToRequest(pageSettings))
            .then(response => response.json())
            .then(histData => {
                setBarData({
                    labels: histData.bins.map(bin => bin/60),
                    datasets: histData.dataSets.map((data, i) => createDataset(pageSettings.filters[i], data))
                });
            })
    }, [histogramEndpoint, createdCount, pageSettings]);

    const chartArea = barData.datasets.filter(dataset => dataset.data.length > 0).length >= 1
        ?    <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={barData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    return (
        <Container>

            <Modal centered={true} show={showSelectChallenge} onHide={() => setShowSelectChallenge(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Select Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Select onChange={onSelectChallenge}>

                        <option key={defaultChallenge.name} value={defaultChallenge.name}>
                            {defaultChallenge.name}
                        </option>

                        {
                            savedChallenges.completed.map(challenge => {
                                return (
                                    <option key={challenge.name} value={challenge.name}>
                                        {challenge.name}
                                    </option>
                                )
                            })
                        }

                    </Form.Select>
                </Modal.Body>
                <Modal.Footer>
                    <div className="d-flex flex-row">
                        <Button className="me-3" variant="primary"
                                onClick={onSelectChallenge}>Select</Button>
                        <Button className="" variant="secondary"
                                onClick={() => setShowSelectChallenge(false)}>Cancel</Button>
                    </div>
                </Modal.Footer>
            </Modal>

            <NavHeader title="Sleep Histogram">
                <Button variant="secondary" disabled={pageSettings.filters.length === histogramColor.length}
                        onClick={ () => setShowSelectChallenge(true) } >
                    <FontAwesomeIcon icon={faPlus} />
                </Button>
            </NavHeader>

            {
                pageSettings.filters.map((filter, i) => {
                    return (
                        <Row key={i} className={"pb-1"}>
                            <Col className="col-10 px-1">
                                <Button className={"w-100 text-start"}>{toShortName(filter.challenge.name)}</Button>
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

export default Histogram2;

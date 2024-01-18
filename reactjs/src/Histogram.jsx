
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
import { mapChallengeDetails, withExactTime} from "./utility/Mapper";
import {emptyChallengeList} from "./utility/Constants";

Chart.register(...registerables)

const histOptions = {
    plugins: {
        legend: {
            display: false
        },
    },
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
            label: displayInfo.challenge.name,
            borderColor: displayInfo.color,
            backgroundColor: displayInfo.color
        };
}

const pageSettingsToRequest = (pageSettings) => {

    const newDataFilters = pageSettings.filters.map((filter) => { return {
            from: SleepDataManager.toIsoString(filter.challenge.exactStart),
            to: SleepDataManager.toIsoString(filter.challenge.exactFinish)
        }}
    );

    return {
        binSize: pageSettings.binSize,
        filters: {
            dataFilters: newDataFilters
        }
    }

}

const last30days = createRange(30);

const defaultChallenge = {
    name: "Last 30 Days",
    description: "Last 30 Days",
    exactStart: last30days.from,
    exactFinish: last30days.to
}

function Histogram(props) {

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
                                                                    ],
                                                                    availableFilters: []
                                                                });

    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeList);
    const [showSelectChallenge, setShowSelectChallenge] = useState(false);

    let[barData, setBarData] = useState({
                                                                    labels: [],
                                                                    datasets: []
                                                                });

    const onSelectChallenge = event => {

        let usedColors = pageSettings.filters.map(filter => filter.color);
        let availableColors = histogramColor.filter(color => ! usedColors.includes(color));

        // event target value is the challenge name, option value has to be a string not an object, so need to find it
        const selectedName = event.target.value;
        const foundChallenge = defaultChallenge.name == selectedName
            ? defaultChallenge
            : savedChallenges.completed.find(saved => saved.name === selectedName);

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.push({
                    challenge: foundChallenge,
                    color: availableColors[0]
                });

        setShowSelectChallenge(false);
        setPageSettings(newPageSettings);
    }

    const availableChallengeFilters = [];
    if( ! isActiveFilter(defaultChallenge)) {
        availableChallengeFilters.push(defaultChallenge);
    }
    savedChallenges.completed.filter(challenge => ! isActiveFilter(challenge))
        .forEach(challenge => availableChallengeFilters.push(challenge));


    function isActiveFilter(searchChallenge) {
        return pageSettings.filters.filter(filter => filter.challenge.name === searchChallenge.name).length > 0
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
            .then(challengeList => mapChallengeDetails(challengeList, withExactTime))
            .then(challengeList => mapChallengeDetails(challengeList, detail => detail.challenge))
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

    const maxFiltersUsed = (pageSettings.filters.length === histogramColor.length);
    const challengesAvailable = (availableChallengeFilters.length > 0);

    const chartArea = barData.datasets.filter(dataset => dataset.data.length > 0).length >= 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={histOptions} data={barData} />
        :   <h1 className="pt-5 mx-auto mw-100 text-center text-secondary">No Data Available</h1>

    return (
        <Container>

            <Modal centered={true} show={showSelectChallenge} onHide={() => setShowSelectChallenge(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Select Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Select onChange={onSelectChallenge}>
                        <option>Select a Challenge</option>
                        {availableChallengeFilters.map(challenge => {
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
                        <Button className="" variant="secondary"
                                onClick={() => setShowSelectChallenge(false)}>Cancel</Button>
                    </div>
                </Modal.Footer>
            </Modal>

            <NavHeader title="Sleep Histogram">
                <Button variant="success" disabled={maxFiltersUsed || ! challengesAvailable}
                        onClick={ () => setShowSelectChallenge(true) } >
                    <FontAwesomeIcon icon={faPlus} />
                </Button>
            </NavHeader>

            {
                pageSettings.filters.map((filter, i) => {
                    return (
                        <Row style={{backgroundColor: filter.color}} key={i} className={"p-2 mb-1 pe-0 border rounded"}>
                            <Col className="px-0 col-10 ">
                                <div >{filter.challenge.name}</div>
                            </Col>
                            <Col className={"px-0"}>
                                <Button variant="warning" className="mx-1" disabled={pageSettings.filters.length === 1} onClick={ () => onRemoveFilter(i) } >
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

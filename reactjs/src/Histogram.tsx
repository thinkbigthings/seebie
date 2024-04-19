import React, {useEffect, useState} from 'react';

import { Chart, registerables } from 'chart.js'

import {Bar} from 'react-chartjs-2';
import Container from "react-bootstrap/Container";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Form from 'react-bootstrap/Form';
import {NavHeader} from "./App";
import {fetchPost, GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus, faRemove} from "@fortawesome/free-solid-svg-icons";
import {useParams} from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import {
    toLocalChallengeDataList,
    toSelectableChallenges
} from "./utility/Mapper";
import {createRange, HISTOGRAM_BIN_SIZE_OPTIONS, HISTOGRAM_COLORS, HISTOGRAM_OPTIONS} from "./utility/Constants";
import {toIsoString} from "./utility/SleepDataManager";
import {ChallengeData} from "./types/challenge.types";

Chart.register(...registerables)

// This comes from the server, the record is List<Long> bins, List<List<Long>> dataSets
// the double array here is such that the first index corresponds to the "outer" list of dataSets
interface HistogramData {
    bins: number[],
    dataSets: number[][]
}

// the resulting object goes into the chartData datasets array
const createDataset = (displayInfo: PageSettingFilters, data: number[]) => {
    return {
            fill: true,
            data: data,
            label: displayInfo.challenge.name,
            borderColor: displayInfo.color,
            backgroundColor: displayInfo.color
        };
}

const pageSettingsToRequest = (pageSettings: PageSettings) => {

    const newDataFilters = pageSettings.filters.map( (filter) => { return {
            from: toIsoString(filter.challenge.exactStart),
            to: toIsoString(filter.challenge.exactFinish)
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

const defaultChallenge: ChallengeData = {
    id: 0,
    name: "Last 30 Days",
    description: "Last 30 Days",
    localStartTime: last30days.from,
    localEndTime: last30days.to,
    exactStart: last30days.from,
    exactFinish: last30days.to
}

interface PageSettingFilters {
    challenge: ChallengeData,
    color: string
}

interface PageSettings {
    binSize: number,
    filters: PageSettingFilters[],
}

interface BarData {
    labels: number[],
    datasets: any[]
}

const defaultPageSettings: PageSettings = {
    binSize: 60,
    filters: [
        {
            challenge: defaultChallenge,
            color: HISTOGRAM_COLORS[0]
        }
    ],
}

function Histogram(props: {createdCount:number}) {

    // TODO createdCount should be named "sleepLoggedCountSinceAppLoad" or something
    // this is so the page is updated when the user logs sleep
    const {createdCount} = props;

    const {username} = useParams();

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;
    const histogramEndpoint = `/api/user/${username}/sleep/histogram`;

    let [pageSettings, setPageSettings] = useState(defaultPageSettings);

    const [availableChallenges, setAvailableChallenges] = useState<ChallengeData[]>([defaultChallenge]);
    const [showSelectChallenge, setShowSelectChallenge] = useState(false);

    let[barData, setBarData] = useState({ labels: [], datasets: [] } as BarData);

    const onSelectChallenge = (event: React.ChangeEvent<HTMLSelectElement>) => {

        let usedColors = pageSettings.filters.map(filter => filter.color);
        let selectableColors = HISTOGRAM_COLORS.filter(color => ! usedColors.includes(color));
        let color = selectableColors[0];

        // event target value is the challenge name, option value has to be a string not an object, so need to find it
        const challenge = availableChallenges.find(saved => saved.name === event.target.value)!;

        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.push({ challenge, color });

        setShowSelectChallenge(false);
        setPageSettings(newPageSettings);
    }


    const availableChallengeFilters = availableChallenges.filter(challenge => ! isActiveFilter(challenge));

    function isActiveFilter(searchChallenge: ChallengeData) {
        return pageSettings.filters.some(filter => filter.challenge.name === searchChallenge.name);
    }

    function onRemoveFilter(i: number) {
        let newPageSettings = structuredClone(pageSettings);
        newPageSettings.filters.splice(i, 1);
        setPageSettings(newPageSettings);
    }

    const updateBinSize = (event: React.ChangeEvent<HTMLSelectElement>) => {
        let newPageState = structuredClone(pageSettings);
        newPageState.binSize = parseInt(event.target.value);
        setPageSettings(newPageState);
    };

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(toLocalChallengeDataList)
            .then(challengeList => toSelectableChallenges(challengeList, defaultChallenge))
            .then(setAvailableChallenges)
            .catch(error => console.log(error));
    }, []);

    useEffect(() => {
        fetchPost(histogramEndpoint, pageSettingsToRequest(pageSettings))
            .then(response => response.json() as Promise<HistogramData>)
            .then(histData => {
                setBarData({
                    labels: histData.bins.map(bin => bin/60),
                    datasets: histData.dataSets.map((data, i) => createDataset(pageSettings.filters[i], data))
                });
            })
    }, [histogramEndpoint, createdCount, pageSettings]);

    const maxFiltersUsed = (pageSettings.filters.length === HISTOGRAM_COLORS.length);
    const challengesAvailable = (availableChallengeFilters.length > 0);

    const chartArea = barData.datasets.filter(dataset => dataset.data.length > 0).length >= 1
        ?   <Bar className="pt-3" datasetIdKey="sleepChart" options={HISTOGRAM_OPTIONS} data={barData} />
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
                            HISTOGRAM_BIN_SIZE_OPTIONS.map(option => {
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

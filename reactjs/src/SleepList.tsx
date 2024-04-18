import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCaretLeft, faCaretRight,} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import Table from "react-bootstrap/Table";
import {Link, useParams} from "react-router-dom";
import {toPagingLabel, useApiGet, PageData} from './hooks/useApiGet.js';
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import {NavHeader} from "./App";
import {SleepDetailDto, SleepDto} from "./types/sleep.types.ts";
import {toLocalSleepData} from "./utility/Mapper.ts";

const minuteToHrMin = (minutes: number) => {
    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

function SleepList(props:{createdCount: number}) {

    const {username} = useParams();

    if (username === undefined) {
        throw new Error("Username is required in the url");
    }

    const {createdCount} = props;

    const sleepUrl = '/api/user/' + username + '/sleep';

    const {data, pagingControls} = useApiGet<SleepDetailDto>(sleepUrl, 7, createdCount);

    console.log(data)

    const pagingControlVisibility = data.totalElements > 0 ? "visible" : "invisible";

    return (

        <Container className="container">

            <NavHeader title="Sleep Listing" />

            <Table striped bordered hover >
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Duration</th>
                    </tr>
                </thead>
                <tbody className="clickable-table">
                    {data.content
                        .map(toLocalSleepData)
                        .map(sleep =>
                            <tr key={sleep.id}>
                                <td>
                                    <Link to={"/users/" + username + "/sleep/" + sleep.id + "/edit" } >
                                        {new Date(sleep.stopTime).toLocaleDateString()}
                                    </Link>
                                </td>
                                <td>{minuteToHrMin(sleep.minutesAsleep)}</td>
                            </tr>
                    )}
                </tbody>
            </Table>
            <ButtonGroup className={"mt-2 " + pagingControlVisibility}>
                <Button variant="primary" disabled={data.first} onClick={ pagingControls.previous }>
                    <FontAwesomeIcon className="app-highlight me-2" icon={faCaretLeft} />Previous
                </Button>
                <div className="page-item disabled border align-middle pt-1 px-3"><span className="page-link">{toPagingLabel(data)}</span></div>
                <Button variant="primary" disabled={data.last} onClick={ pagingControls.next}>
                    <FontAwesomeIcon className="app-highlight me-2" icon={faCaretRight} />Next
                </Button>
            </ButtonGroup>
        </Container>

    );
}

export default SleepList;

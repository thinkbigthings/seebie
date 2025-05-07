import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCaretLeft, faCaretRight,} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import Table from "react-bootstrap/Table";
import {Link, useParams} from "react-router-dom";
import {isFirst, isLast, toPagingLabel, useApiGet} from './hooks/useApiGet.js';
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import {NavHeader} from "./App";
import {toLocalSleepData} from "./utility/Mapper";
import {SleepDetailDto} from "./types/sleep.types.ts";

const minuteToHrMin = (minutes: number) => {
    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

function SleepList(props:{createdCount: number}) {

    const {publicId} = useParams();

    const {createdCount} = props;

    const sleepUrl = `/api/user/${publicId}/sleep`

    const {data, pagingControls} = useApiGet<SleepDetailDto>(sleepUrl, 7, createdCount);

    const pagingControlVisibility = data.page.totalElements > 0 ? "visible" : "invisible";

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
                                    <Link to={`/users/${publicId}/sleep/${sleep.id}/edit` } >
                                        {sleep.stopTime.toLocalDate().toString()}
                                    </Link>
                                </td>
                                <td>{minuteToHrMin(sleep.minutesAsleep)}</td>
                            </tr>
                    )}
                </tbody>
            </Table>
            <ButtonGroup className={"mt-2 " + pagingControlVisibility}>
                <Button variant="primary" disabled={isFirst(data.page)} onClick={ pagingControls.previous }>
                    <FontAwesomeIcon className="app-highlight me-2" icon={faCaretLeft} />Previous
                </Button>
                <div className="page-item disabled border align-middle pt-1 px-3"><span className="page-link">{toPagingLabel(data)}</span></div>
                <Button variant="primary" disabled={isLast(data.page)} onClick={ pagingControls.next}>
                    <FontAwesomeIcon className="app-highlight me-2" icon={faCaretRight} />Next
                </Button>
            </ButtonGroup>
        </Container>

    );
}

export default SleepList;

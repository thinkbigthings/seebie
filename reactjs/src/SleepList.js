import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCaretLeft, faCaretRight,} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import Table from "react-bootstrap/Table";
import {Link} from "react-router-dom";
import {toPagingLabel, useApiGet} from './useApiGet.js';
import useCurrentUser from "./useCurrentUser";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import SleepDataManager from "./SleepDataManager";

function SleepList(props) {

    const {reloadCount} = props;

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [data, pagingControls] = useApiGet(sleepUrl, 7, reloadCount);

    const pagingControlVisibility = data.totalElements > 0 ? "visible" : "invisible";

    return (

        <Container className="container p-0">
            <Table striped bordered hover >
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Duration</th>
                    </tr>
                </thead>
                <tbody className="clickable-table">
                    {data.content
                        .map(sleep => { sleep.sleepData = SleepDataManager.parse(sleep.sleepData); return sleep; })
                        .map(sleep =>
                            <tr key={sleep.id}>
                                <td>
                                    <Link to={"/users/" + currentUser.username + "/sleep/" + sleep.id + "/edit" } >
                                        {new Date(sleep.sleepData.stopTime).toLocaleDateString()}
                                    </Link>
                                </td>
                                <td>{SleepDataManager.formatDuration(sleep.sleepData.startTime, sleep.sleepData.stopTime)}</td>
                            </tr>
                    )}
                </tbody>
            </Table>
            <ButtonGroup className={"mt-2 " + pagingControlVisibility}>
                <Button variant="primary" disabled={data.first} onClick={ pagingControls.previous }>
                    <FontAwesomeIcon className="me-2" icon={faCaretLeft} />Previous
                </Button>
                <div className="page-item disabled border align-middle pt-1 px-3"><span className="page-link">{toPagingLabel(data)}</span></div>
                <Button variant="primary" disabled={data.last} onClick={ pagingControls.next}>
                    <FontAwesomeIcon className="me-2" icon={faCaretRight} />Next
                </Button>
            </ButtonGroup>
        </Container>

    );
}

export default SleepList;

import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faCaretLeft,
    faCaretRight,
    faEdit,
    faTrash,
} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import {CreateSleepSession} from "./CreateSleepSession";
import Table from "react-bootstrap/Table";
import {Link} from "react-router-dom";
import {useApiGet, toPagingLabel} from './useApiGet.js';
import useCurrentUser from "./useCurrentUser";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import SleepDataManager from "./SleepDataManager";
import useApiDelete from "./useApiDelete";

function Home() {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [data, pagingControls] = useApiGet(sleepUrl);

    const callDelete = useApiDelete();

    const deleteById = (sleepId) => {
        callDelete("/user/" + currentUser.username + "/sleep/" + sleepId)
            .then(pagingControls.reload);
    }

    const visibility = data.totalElements > 0 ? "visible" : "invisible";

    return (
        <div className="container mt-3">

            <CreateSleepSession onSave={pagingControls.reload}/>

            <Container className="container mt-3 p-0">
                <Table striped bordered hover >
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Duration</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
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
                                    <td className="d-flex flex-row-reverse">
                                        <FontAwesomeIcon className="px-3 " icon={faTrash} onClick={() => deleteById(sleep.id)} />
                                    </td>
                                </tr>
                        )}
                    </tbody>
                </Table>
            </Container>

            <ButtonGroup className={"mt-2 " + visibility}>
                <Button variant="primary" disabled={data.first} onClick={ pagingControls.previous }>
                    <FontAwesomeIcon className="me-2" icon={faCaretLeft} />Previous
                </Button>
                <div className="page-item disabled border align-middle pt-1 px-3"><span className="page-link">{toPagingLabel(data)}</span></div>
                <Button variant="primary" disabled={data.last} onClick={ pagingControls.next}>
                    <FontAwesomeIcon className="me-2" icon={faCaretRight} />Next
                </Button>
            </ButtonGroup>

        </div>
    );
}

export default Home;

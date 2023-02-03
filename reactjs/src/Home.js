import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faCaretLeft,
    faCaretRight,
    faDeleteLeft,
    faEdit,
    faHome,
    faTrash,
    faUserEdit
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

const initialPage = {
    content: [],
    first: true,
    last: true,
    totalElements: 0,
    pageable: {
        offset: 0,
        pageNumber: 0,
        pageSize: 10,
    },
    numberOfElements: 0,
}

function Home() {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [data, pagingControls] = useApiGet(sleepUrl, initialPage);

    const callDelete = useApiDelete();

    const deleteById = (sleepId) => {
        callDelete("/user/" + currentUser.username + "/sleep/" + sleepId)
            .then(pagingControls.reload);
    }

    return (
        <div className="container mt-3">
            <h1>Seebie<FontAwesomeIcon icon={faHome} /></h1>

            <CreateSleepSession onSave={pagingControls.reload}/>

            <Container className="container mt-3">
                <Table striped bordered hover >
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Duration</th>
                            <th>Edit</th>
                            <th>Delete</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content
                            .map(sleep => { sleep.sleepData = SleepDataManager.parse(sleep.sleepData); return sleep; })
                            .map(sleep =>
                                <tr key={sleep.id}>
                                    <td>{new Date(sleep.sleepData.stopTime).toLocaleDateString()}</td>
                                    <td>{SleepDataManager.formatDuration(sleep.sleepData.startTime, sleep.sleepData.stopTime)}</td>
                                    <td className="text-center">
                                        <Link to={"/users/" + currentUser.username + "/sleep/" + sleep.id + "/edit" } className="btn btn-primary">
                                            <FontAwesomeIcon className="px-3" icon={faEdit} />
                                        </Link>
                                    </td>
                                    <td className="text-center">
                                        <FontAwesomeIcon className="px-3 " icon={faTrash} onClick={() => deleteById(sleep.id)} />
                                    </td>
                                </tr>
                        )}
                    </tbody>
                </Table>
            </Container>

            <ButtonGroup className="mt-2">
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

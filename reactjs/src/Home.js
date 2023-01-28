import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCaretLeft, faCaretRight, faHome, faUserEdit} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import CreateSleepSession from "./CreateSleepSession";
import Table from "react-bootstrap/Table";
import {Link} from "react-router-dom";
import useApiGet from "./useApiGet";
import useCurrentUser from "./useCurrentUser";
import copy from "./Copier";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";

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

const minuteToHrMin = (minutes) => {
    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

function Home() {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [data, setUrl, reload] = useApiGet(sleepUrl + '?page=0&size=10', initialPage);


    function movePage(amount) {
        let pageable = copy(data.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        setUrl(sleepUrl + '?' + 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize);
    }

    const firstElementInPage = data.pageable.offset + 1;
    const lastElementInPage = data.pageable.offset + data.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + data.totalElements;

    return (
        <div className="container mt-3">
            <h1>Seebie<FontAwesomeIcon icon={faHome} /></h1>

            <CreateSleepSession onSave={reload}/>

            <Container className="container mt-3">
                <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Duration</th>
                            <th>Edit</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.content.map(sleep =>
                            <tr key={sleep.id}>
                                <td>{sleep.sleepData.dateAwakened}</td>
                                <td>{minuteToHrMin(sleep.sleepData.minutes)}</td>
                                <td>
                                    <Link to={sleepUrl + '/' + sleep.id } className="btn btn-primary">
                                        <FontAwesomeIcon className="me-2" icon={faUserEdit} />Edit
                                    </Link>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            </Container>

            <ButtonGroup className="mt-2">
                <Button variant="primary" disabled={data.first} className={"btn btn-primary "} onClick={ () => movePage(-1) }>
                    <FontAwesomeIcon className="me-2" icon={faCaretLeft} />Previous
                </Button>
                <div className="page-item disabled"><span className="page-link">{currentPage}</span></div>
                <Button variant="primary" disabled={data.last} className={"btn btn-primary "} onClick={ () => movePage(1) }>
                    <FontAwesomeIcon className="me-2" icon={faCaretRight} />Next
                </Button>
            </ButtonGroup>

        </div>
    );
}

export default Home;

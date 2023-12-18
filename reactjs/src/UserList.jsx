import React, {useState} from 'react';

import { Link } from 'react-router-dom';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';

import {useApiGet, toPagingLabel} from './hooks/useApiGet.jsx';
import CreateUser from "./CreateUser";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faCaretLeft,
    faCaretRight,
    faChartLine,
    faChartSimple,
    faList,
    faTools
} from "@fortawesome/free-solid-svg-icons";
import Table from "react-bootstrap/Table";
import {NavHeader} from "./App";


function UserList() {

    const [reloadCount, setReloadCount] = useState(0);
    const [data, pagingControls] = useApiGet('/api/user', 10, reloadCount);

    const visibility = data.totalElements > 0 ? "visible" : "invisible";

    return (
        <Container>

            <NavHeader title="Users" >
                <CreateUser onSave={() => setReloadCount(reloadCount + 1)}  />
            </NavHeader>


            <Container className="container mt-3 px-0">
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Pages</th>
                    </tr>
                    </thead>
                    <tbody className="clickable-table">
                    {data.content.map(user =>
                        <tr key={user.username}>
                            <td>

                                <Link to={ "/users/" + user.username + "/edit" } >
                                    {user.displayName}
                                </Link>
                            </td>
                            <td>
                                <ButtonGroup>
                                    <Button className="p-0 me-1" >
                                        <Link className="p-0 ps-1 " to={ "/users/" + user.username + "/sleep/list" } >
                                            <FontAwesomeIcon className="me-2" icon={faList} />
                                        </Link>
                                    </Button>
                                    <Button className="p-0 me-1">
                                        <Link className="p-0 ps-1 " to={ "/users/" + user.username + "/sleep/chart" } >
                                            <FontAwesomeIcon className="me-2" icon={faChartLine} />
                                        </Link>
                                    </Button>
                                    <Button className="p-0 me-1">
                                        <Link className="p-0 ps-1 " to={ "/users/" + user.username + "/histogram" } >
                                            <FontAwesomeIcon className="me-2" icon={faChartSimple} />
                                        </Link>
                                    </Button>
                                    <Button className="p-0 ">
                                        <Link className="p-0 ps-1 " to={ "/users/" + user.username + "/tools" } >
                                            <FontAwesomeIcon className="me-2" icon={faTools} />
                                        </Link>
                                    </Button>
                                </ButtonGroup>


                            </td>
                        </tr>
                    )}
                    </tbody>
                </Table>
            </Container>

            <ButtonGroup className={"mt-2 " + visibility}>
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

export default UserList;

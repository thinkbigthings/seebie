import React from 'react';

import { Link } from 'react-router-dom';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';

import copy from './Copier.js';
import CreateUser from "./CreateUser";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserEdit, faCaretLeft, faCaretRight} from "@fortawesome/free-solid-svg-icons";
import useApiGet from "./useApiGet";
import Table from "react-bootstrap/Table";

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

function UserList() {

    const [data, setUrl, reload] = useApiGet('/user?page=0&size=10', initialPage)

    function movePage(amount) {
        let pageable = copy(data.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        setUrl('/user?' + 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize)
    }


    const firstElementInPage = data.pageable.offset + 1;
    const lastElementInPage = data.pageable.offset + data.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + data.totalElements;

    return (
        <div className="container mt-3">
            <h1>User Management</h1>

            <CreateUser onSave={reload} />

            <Container className="container mt-3">
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>User</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.content.map(user =>
                        <tr key={user.username}>
                            <td>{user.displayName}</td>
                            <td>
                                <Link to={"/users/" + user.username + "/edit" } className="btn btn-primary">
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

export default UserList;

import React from 'react';

import { Link } from 'react-router-dom';

import ButtonGroup  from 'react-bootstrap/ButtonGroup';
import Button       from "react-bootstrap/Button";
import Container    from 'react-bootstrap/Container';
import Row          from 'react-bootstrap/Row';
import Col          from 'react-bootstrap/Col';

import useApiPost from "./useApiPost";

import copy from './Copier.js';
import CreateStore from "./CreateStore";
import useApiLoader from "./useApiLoader";
import CenteredSpinner from "./CenteredSpinner";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faUserEdit, faCaretLeft, faCaretRight} from "@fortawesome/free-solid-svg-icons";

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

function StoreList() {

    const post = useApiPost();
    const {setUrl, isLoading, isLongRequest, fetchedData} = useApiLoader('/store?page=0&size=10', initialPage);

    let fetchStores = (pageable) => {
        setUrl('/store?' + pageQuery(pageable));
    };

    function scrape() {
        post("/store/scrape", {}).then(result => {});
    }

    const pageQuery = (pageable) => {
        return 'page=' + pageable.pageNumber + '&size=' + pageable.pageSize;
    }

    function movePage(amount) {
        let pageable = copy(fetchedData.pageable);
        pageable.pageNumber = pageable.pageNumber + amount;
        fetchStores(pageable);
    }

    const firstElementInPage = fetchedData.pageable.offset + 1;
    const lastElementInPage = fetchedData.pageable.offset + fetchedData.numberOfElements;
    const currentPage = firstElementInPage + "-" + lastElementInPage + " of " + fetchedData.totalElements;

    if(isLoading && ! isLongRequest) { return <div />; }

    if(isLoading && isLongRequest) {   return <CenteredSpinner /> ; }

    return (
        <>

            <div className="container mt-3">
                <h1>Stores</h1>

                <CreateStore />

                <Button variant="primary" className={"btn btn-primary "} onClick={ () => scrape() }>
                    <FontAwesomeIcon className="me-2" icon={faUserEdit} />Update
                </Button>

                <Container className="container mt-3">
                    {fetchedData.content.map(store =>
                        <Row key={store.name} className="pt-2 pb-2 border-bottom border-top ">
                            <Col >{store.name}</Col>
                            <Col xs={2}>
                                <Button variant="primary" className={"btn btn-primary "} onClick={ () => console.log('Edit Store goes here') }>
                                    <FontAwesomeIcon className="me-2" icon={faUserEdit} />Edit
                                </Button>
                            </Col>
                        </Row>
                    )}
                </Container>

                <ButtonGroup className="mt-2">
                    <Button variant="primary" disabled={fetchedData.first} className={"btn btn-primary "} onClick={ () => movePage(-1) }>
                        <FontAwesomeIcon className="me-2" icon={faCaretLeft} />Previous
                    </Button>
                    <div className="page-item disabled"><span className="page-link">{currentPage}</span></div>
                    <Button variant="primary" disabled={fetchedData.last} className={"btn btn-primary "} onClick={ () => movePage(1) }>
                        <FontAwesomeIcon className="me-2" icon={faCaretRight} />Next
                    </Button>
                </ButtonGroup>
            </div>
        </>
    );
}

export default StoreList;

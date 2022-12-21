import React from 'react';

import CenteredSpinner from "./CenteredSpinner";
import useApiLoader from "./useApiLoader";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome} from "@fortawesome/free-solid-svg-icons";

import Container from "react-bootstrap/Container";

const initialData = {
    users: {
        count:0
    }
};

function Home() {

    const {isLoading, isLongRequest, fetchedData} = useApiLoader('/actuator/info', initialData);

    if(isLoading && ! isLongRequest) { return <div />; }

    if(isLoading && isLongRequest) {   return <CenteredSpinner /> ; }

    return (
        <Container className="pt-5 pb-3">
            <h1>Seebie<FontAwesomeIcon icon={faHome} /></h1>
            <p>
                This is a sleep analysis tool to help you find your best sleep.
            </p>
            <p>
                There are {fetchedData.users.count} users in the system.
            </p>
        </Container>
    );
}

export default Home;

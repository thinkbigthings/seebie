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
            <h1>Zero Downtime Demo<FontAwesomeIcon icon={faHome} /></h1>
            <p>
                This is a sample app that manages users. It is a starting point for an app
                and a demonstration of doing zero downtime deployment.
            </p>
            <p>
                There are {fetchedData.users.count} users in the system.
            </p>
        </Container>
    );
}

export default Home;

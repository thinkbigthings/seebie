import React from 'react';

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";

import useApiGet from "./useApiGet";


const initialData = {
    users: {
        count:0
    }
};

function Home() {

    const [data] = useApiGet('/actuator/info', initialData);

    return (
        <Container className="pt-5 pb-3">
            <h1>Seebie<FontAwesomeIcon icon={faHome} /></h1>
            <p>
                This is a sleep analysis tool to help you find your best sleep.
            </p>
            <p>
                There are {data.users.count} users in the system.
            </p>
        </Container>
    );
}

export default Home;

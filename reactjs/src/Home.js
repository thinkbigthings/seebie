import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";
import CreateSleepSession from "./CreateSleepSession";


function Home() {


    return (
        <Container className="pt-5 pb-3">

            <h1>Seebie<FontAwesomeIcon icon={faHome} /></h1>
            <p>
                This is a sleep analysis tool to help you find your best sleep.
            </p>
            <p>
                <CreateSleepSession />
            </p>
        </Container>
    );
}

export default Home;

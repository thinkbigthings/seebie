import React, {useEffect, useState} from 'react';

import {GET} from "./BasicHeaders";
import {Accordion} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";

function Home() {

    let [info, setInfo] = useState({})
    let [migrations, setMigrations] = useState({})
    let [health, setHealth] = useState({})


    // Links could also be retrieved from a call to "/actuator"

    useEffect(() => {
        fetch('/actuator/info', GET)
            .then(response => response.json())
            .then(setInfo)
    }, [setInfo]);

    useEffect(() => {
        fetch('/actuator/flyway', GET)
            .then(response => response.json())
            .then(flywayRoot => flywayRoot.contexts.application.flywayBeans.flyway.migrations)
            .then(setMigrations)
    }, [setMigrations]);

    useEffect(() => {
        fetch('/actuator/health', GET)
            .then(response => response.json())
            .then(setHealth)
    }, [setHealth]);

    return (
        <Container>

            <NavHeader title="System" />

            <Accordion defaultActiveKey="0">
                <Section  eventKey="0" header="Info" json={info} />
                <Section  eventKey="1" header="Flyway" json={migrations} />
                <Section  eventKey="2" header="Health" json={health} />
            </Accordion>
        </Container>
    );
}

function Section(props) {

    const {eventKey, header, json} = props;

    return (
        <Accordion.Item eventKey={eventKey}>
            <Accordion.Header>{header}</Accordion.Header>
            <Accordion.Body>
                <Container className={"overflow-x-auto"}><pre>{JSON.stringify(json, null, 2) }</pre></Container>
            </Accordion.Body>
        </Accordion.Item>
    );
}

export default Home;

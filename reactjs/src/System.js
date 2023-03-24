import React, {useEffect, useState} from 'react';

import ReactJson from 'react-json-view'
import {GET} from "./BasicHeaders";
import {Accordion} from "react-bootstrap";
import Container from "react-bootstrap/Container";

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

    // https://www.npmjs.com/package/react-json-view

    return (
        <Container>

            <h1>System</h1>

            <Accordion defaultActiveKey="0">
                <Section  eventKey="0" header="Info" json={info} name={"info"} />
                <Section  eventKey="1" header="Flyway" json={migrations} name={"flyway"} />
                <Section  eventKey="2" header="Health" json={health} name={"health"} />
            </Accordion>
        </Container>
    );
}

function Section(props) {

    const {eventKey, header, name, json} = props;

    return (
        <Accordion.Item eventKey={eventKey}>
            <Accordion.Header>{header}</Accordion.Header>
            <Accordion.Body>
                <ReactJson name={name} src={json} displayDataTypes={false} displayObjectSize={false} theme={"twilight"} collapsed={true}/>
            </Accordion.Body>
        </Accordion.Item>
    );
}

export default Home;

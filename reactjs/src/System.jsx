import React, {useEffect, useState} from 'react';

import {GET} from "./utility/BasicHeaders";
import {Accordion} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";

function Home() {

    let [info, setInfo] = useState({})
    let [migrations, setMigrations] = useState({})
    let [health, setHealth] = useState({})


    // Links could also be retrieved from a call to "/api/actuator"

    useEffect(() => {
        fetch('/api/actuator/info', GET)
            .then(response => response.json())
            .then(json => [
                    'BRANCH',
                    json.git.branch,
                    'COMMIT',
                    json.git.commit.time,
                    json.git.commit.message.short,
                    json.git.commit.id.abbrev,
                    'APP',
                    'Users : ' + json.app.count.user,
                    'API : ' + json.app.version.apiVersion
                ])
            .then(setInfo)
    }, []);

    useEffect(() => {
        fetch('/api/actuator/flyway', GET)
            .then(response => response.json())
            .then(flywayRoot => flywayRoot.contexts.application.flywayBeans.flyway.migrations)
            .then(migrations => migrations.map(migration => migration.script + (migration.state === 'SUCCESS' ? ' ✓' : 'X') ))
            .then(setMigrations)
    }, []);

    useEffect(() => {
        fetch('/api/actuator/health', GET)
            .then(response => response.json())
            .then(setHealth)
    }, []);

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
                <Container className={"overflow-x-auto"}>
                    <pre>
                        {JSON.stringify(json, null, 2) }
                    </pre></Container>
            </Accordion.Body>
        </Accordion.Item>
    );
}

export default Home;

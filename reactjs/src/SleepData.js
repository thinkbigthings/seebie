import React from 'react';
import {CreateSleepSession} from "./CreateSleepSession";
import {Tab, Tabs} from "react-bootstrap";
import SleepList from "./SleepList";
import SleepChart from "./SleepChart";

function SleepData() {


    // the unmountOnExit property on Tabs is set here so that each Tab content component does not make http calls
    // unless it is actually being rendered
    return (
        <div id="sleep-data-container" className="container mt-3 mb-3">

            <CreateSleepSession />

            <Tabs unmountOnExit={true} fill defaultActiveKey="list" id="sleep-data-tabs" className="mb-3" >
                <Tab eventKey="list" title="List">
                    <SleepList />
                </Tab>
                <Tab eventKey="chart" title="Chart">
                    <SleepChart />
                </Tab>
            </Tabs>

        </div>
    );
}

export default SleepData;

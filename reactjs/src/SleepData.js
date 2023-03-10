import React, {useState} from 'react';
import {CreateSleepSession} from "./CreateSleepSession";
import {Tab, Tabs} from "react-bootstrap";
import SleepList from "./SleepList";
import SleepChart from "./SleepChart";

function SleepData() {


    // Refresh data view if a sleep session is logged that affects the view
    let [reloadCount, setReloadCount] = useState(0);

    // the unmountOnExit property on Tabs is set here so that each Tab content component is not rendered
    // and does not make http calls unless it is actually active
    return (
        <div id="sleep-data-container" className="container mt-3 mb-3">

            <CreateSleepSession onSave={() => setReloadCount(reloadCount + 1)} />

            <Tabs unmountOnExit={true} fill defaultActiveKey="list" id="sleep-data-tabs" className="mb-3" >
                <Tab eventKey="list" title="List">
                    <SleepList reloadCount = {reloadCount} />
                </Tab>
                <Tab eventKey="chart" title="Chart">
                    <SleepChart reloadCount = {reloadCount} />
                </Tab>
            </Tabs>

        </div>
    );
}

export default SleepData;

import React from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import DatePickerButton from "./component/DatePickerButton";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SleepDataManager from "./SleepDataManager";

function SleepForm(props) {

    const {setSleepData, sleepData} = props;

    function updateSleepSession(updateValues) {

        let updatedSleep = {...sleepData, ...updateValues};

        // use the local time without the offset for display purposes
        let localStartTime = SleepDataManager.toIsoString(updatedSleep.localStartTime).substring(0, 19);
        let localStopTime = SleepDataManager.toIsoString(updatedSleep.localStopTime).substring(0, 19);
        updatedSleep.startTime = localStartTime + sleepData.startTime.substring(19);
        updatedSleep.stopTime = localStopTime + sleepData.stopTime.substring(19);

        if(SleepDataManager.isDataValid(updatedSleep)) {
            setSleepData(updatedSleep);
        }
    }

    return (
        <Container id="userFormId" className="p-0">
            <Row className={"pb-2"}>
                <Col md={6} className={"col-4 "}>
                    <label htmlFor="dateStart" >Went to Bed</label>
                </Col>
                <Col md={6} className={"col-8 "}>
                    <DatePickerButton selected={sleepData.localStartTime}
                                      onChange={ date => updateSleepSession({localStartTime : date })} />
                </Col>
            </Row>
            <Row className={"pb-2"}>
                <Col md={6} className={"col-4 "}>
                    <label htmlFor="dateEnd" >Got Up</label>
                </Col>
                <Col md={6} className={"col-8 "}>
                    <DatePickerButton selected={sleepData.localStopTime}
                                  onChange={ date => updateSleepSession({localStopTime : date })} />
                </Col>
            </Row>
            <Row className={"pb-2"}>
                <Col md={6} className={"col-4 pe-0"}>
                    <label htmlFor="minutesAwake" className="">Minutes Awake</label>
                </Col>
                <Col md={6} className={"col-8 "}>
                    <input type="text" className="form-control" id="minutesAwake"
                               placeholder="Minutes Awake"
                               value={sleepData.minutesAwake}
                               onChange={e => updateSleepSession({minutesAwake : e.target.value})} />
                </Col>
            </Row>
            <Row className={"pb-2"}>
                <Col md={6} className={"col-12"}>
                    {/*TODO make this an info popup on click, with an info icon next to minutes*/}
                    Total time you were awake during the night including time to fall asleep and time awake at night
                </Col>
            </Row>
            <Row className={"pb-2"}>
                <Col md={6}>
                    <textarea rows="8" className="form-control" id="notes" placeholder="Notes"
                              value={sleepData.notes}
                              onChange={e => updateSleepSession({notes : e.target.value })} />
                </Col>
            </Row>

        </Container>

    );
}

export {SleepForm};

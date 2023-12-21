import React from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import DatePickerButton from "./component/DatePickerButton";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SleepDataManager from "./SleepDataManager";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import InfoModalButton from "./component/InfoModalButton";

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
            <Row className={"pb-4"}>
                <Col md={6} className={"col-4 pe-0"}>
                    <label htmlFor="minutesAwake" className="">Minutes Awake</label>
                </Col>
                <Col md={5} className={"col-6 "}>
                    <input type="text" className="form-control" id="minutesAwake"
                               placeholder="Minutes Awake"
                               value={sleepData.minutesAwake}
                               onChange={e => updateSleepSession({minutesAwake : e.target.value})} />
                </Col>
                <Col md={1} className={"col-1"}>
                    <InfoModalButton titleText="Minutes Awake"
                                     modalText="This includes the time it takes to fall asleep,
                                      plus the amount of time spent awake during the night" />
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

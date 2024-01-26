import React from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import DatePickerButton from "./component/DatePickerButton";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import SleepDataManager from "./SleepDataManager";
import InfoModalButton from "./component/InfoModalButton";
import Form from "react-bootstrap/Form";

function isNumericString(value) {
    return /^\d+$/.test(value);
}

function SleepForm(props) {

    const {setSleepData, sleepData, setDataValid} = props;

    const [minutesAwakeValidity, setMinutesAwakeValidity] = React.useState(true);

    const updateSleepSession = (updateValues) => {

        let updatedSleep = {...sleepData, ...updateValues};

        // use the local time without the offset for display purposes
        let localStartTime = SleepDataManager.toIsoString(updatedSleep.localStartTime).substring(0, 19);
        let localStopTime = SleepDataManager.toIsoString(updatedSleep.localStopTime).substring(0, 19);
        updatedSleep.startTime = localStartTime + sleepData.startTime.substring(19);
        updatedSleep.stopTime = localStopTime + sleepData.stopTime.substring(19);

        setMinutesAwakeValidity(isNumericString(updatedSleep.minutesAwake));
        setDataValid(isNumericString(updatedSleep.minutesAwake));
        setSleepData(updatedSleep);
    }

    return (
        <Container id="sleepFormId" className="p-0">
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
            <Row>
                <Col md={6} className={"col-4 pe-0"}>
                    <label htmlFor="minutesAwake" className="">Minutes Awake</label>
                </Col>
                <Col md={5} className={"col-6 "}>
                    <Form.Control
                        type="text"
                        placeholder="Minutes Awake"
                        value={sleepData.minutesAwake}
                        name="minutesAwakeField"
                        onChange={e => updateSleepSession({minutesAwake: e.target.value})}
                        isValid={minutesAwakeValidity}
                        isInvalid={ ! minutesAwakeValidity}
                    />
                    <Form.Control.Feedback type="invalid"
                                           className={"mh-24px d-block " + ((! minutesAwakeValidity) ? 'visible' : 'invisible')}>
                        Minutes Awake must be a number.
                    </Form.Control.Feedback>
                </Col>
                <Col md={1} className={"col-1"}>
                    <InfoModalButton titleText="Minutes Awake"
                                     modalText="This includes the time it takes to fall asleep,
                                      plus the amount of time spent awake during the night"/>
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

export {SleepForm, isNumericString};

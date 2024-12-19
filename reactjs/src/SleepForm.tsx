import React, {useState} from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import DatePickerButton from "./component/DatePickerButton";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import InfoModalButton from "./component/InfoModalButton";
import Form from "react-bootstrap/Form";
import {SleepData} from "./types/sleep.types";
import {fromLocalDateTime, toLocalDateTime} from "./utility/Mapper.ts";

function isNumericString(value:string) {
    return /^\d+$/.test(value);
}

interface SleepFormFields {
    notes: string,
    minutesAwake: string,
    localStartTime: Date,
    localStopTime: Date
}

const toForm = (sleep:SleepData):SleepFormFields => {
    return {
        notes: sleep.notes,
        minutesAwake: sleep.minutesAwake.toString(),
        localStartTime: fromLocalDateTime(sleep.startTime),
        localStopTime: fromLocalDateTime(sleep.stopTime)
    }
}

function SleepForm(props:{setSleepData: (sleep:SleepData) => void, sleepData:SleepData, setDataValid: (valid:boolean) => void} ) {

    const {setSleepData, sleepData, setDataValid} = props;

    const [formFields, setFormFields] = useState(toForm(sleepData));

    const [minutesAwakeValidity, setMinutesAwakeValidity] = React.useState(true);

    const updateSleepSession = (updateValues: Partial<SleepFormFields>) => {

        let updatedFormFields: SleepFormFields = {...formFields, ...updateValues};

        let minutesAwakeNumeric = isNumericString(updatedFormFields.minutesAwake);
        let minutesAwakeDurationValid = false;
        if(minutesAwakeNumeric) {
            let minutesInBed = (updatedFormFields.localStopTime.getTime() - updatedFormFields.localStartTime.getTime()) / 60000;
            let minutesAwake = parseInt(updatedFormFields.minutesAwake);
            if(minutesAwake <= minutesInBed) {
                minutesAwakeDurationValid = true;
            }
        }
        let isFormValid = minutesAwakeNumeric && minutesAwakeDurationValid;

        setMinutesAwakeValidity(isFormValid); // shows the validation message
        setDataValid(isFormValid); // can enable or disable containing component's save button
        if(isFormValid) {
            setSleepData({
                ...sleepData,
                notes: updatedFormFields.notes,
                minutesAwake: parseInt(updatedFormFields.minutesAwake),
                startTime: toLocalDateTime(updatedFormFields.localStartTime),
                stopTime: toLocalDateTime(updatedFormFields.localStopTime)
            });
        }
        setFormFields(updatedFormFields);
    }

    return (
        <Container id="sleepFormId" className="p-0">
            <Row className={"pb-2"}>
                <Col md={6} className={"col-4 "}>
                    <label htmlFor="dateStart" >Went to Bed</label>
                </Col>
                <Col md={6} className={"col-8 "}>
                    <DatePickerButton selected={formFields.localStartTime}
                                      onChange={ (date:Date) => updateSleepSession({localStartTime : date })} />
                </Col>
            </Row>
            <Row className={"pb-2"}>
                <Col md={6} className={"col-4 "}>
                    <label htmlFor="dateEnd" >Got Up</label>
                </Col>
                <Col md={6} className={"col-8 "}>
                    <DatePickerButton selected={formFields.localStopTime}
                                  onChange={ (date:Date) => updateSleepSession({localStopTime : date })} />
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
                        value={formFields.minutesAwake}
                        name="minutesAwakeField"
                        onChange={e => updateSleepSession({minutesAwake: e.target.value})}
                        isValid={minutesAwakeValidity}
                        isInvalid={ ! minutesAwakeValidity}
                    />
                    <Form.Control.Feedback type="invalid"
                                           className={"mh-24px d-block " + ((! minutesAwakeValidity) ? 'visible' : 'invisible')}>
                        {"Must be a number less than minutes in bed"}
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
                    <textarea rows={8} className="form-control" id="notes" placeholder="Notes"
                              value={formFields.notes}
                              onChange={e => updateSleepSession({notes : e.target.value })} />
                </Col>
            </Row>

        </Container>

    );
}

export {SleepForm};

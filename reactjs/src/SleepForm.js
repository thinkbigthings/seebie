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
        <Container id="userFormId" className="ps-0 ">
            <Row>
                <Col md={6} className={"pe-4"}>
                    <div className="mb-3">
                        <label htmlFor="dateStart" className="form-label">Time Fell Asleep</label>
                        <DatePickerButton selectTime={sleepData.localStartTime} onSelection={ date => updateSleepSession({localStartTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="dateEnd" className="form-label">Time Woke Up</label>
                        <DatePickerButton selectTime={sleepData.localStopTime} onSelection={ date => updateSleepSession({localStopTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="minutesAwake" className="form-label">Minutes Awake During Sleep Period</label>
                        <input type="text" className="form-control w-25" id="minutesAwake"
                               placeholder="Minutes Awake During Sleep Period"
                               value={sleepData.minutesAwake}
                               onChange={e => updateSleepSession({minutesAwake : e.target.value})} />
                    </div>
                </Col>
                <Col md={6} className={"pe-0"}>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea rows="8" className="form-control" id="notes" placeholder="Notes"
                                  value={sleepData.notes}
                                  onChange={e => updateSleepSession({notes : e.target.value })} />
                    </div>
                </Col>
            </Row>

        </Container>

    );
}

export {SleepForm};

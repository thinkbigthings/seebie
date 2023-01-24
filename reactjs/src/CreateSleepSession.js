import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";

import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';

const minutesBetween = (date1, date2) => {
    let diff = (date2.getTime() - date1.getTime()) / 1000;
    diff /= 60;
    return Math.abs(Math.round(diff));
}

function CreateSleepSession() {


    const {currentUser} = useCurrentUser();


    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    const formSleepData = {
        startDate: yesterday,
        endDate: today,
        notes: '',
        outOfBed: 0
    }

    const sleepUrl = '/user/' + currentUser.username + '/sleep';


    const post = useApiPost();
    const [showLogSleep, setShowLogSleep] = useState(false);
    const [sleepSession, setSleepSession] = useState(formSleepData);

    console.log("current sleep session: " + JSON.stringify(sleepSession))

    const onCreate = (formData) => {

        const minutesSleepSession = minutesBetween(formData.endDate, formData.startDate);

        const formSleepData = {
            dateAwakened: formData.endDate,
            minutes: minutesSleepSession,
            notes: formData.notes,
            outOfBed: formData.outOfBed,
            tags: []
        }

        const requestBody = JSON.stringify(formSleepData);
        console.log("posting " + requestBody);

        post(sleepUrl, requestBody)
            .then(result => setShowLogSleep(false));
    }

    function updateSleepSession(updateValues) {
        setSleepSession( {...sleepSession, ...updateValues});
    }

    function onHide() {
        setSleepSession(formSleepData);
        setShowLogSleep(false);
    }

    function onConfirm() {
        setSleepSession(formSleepData);
        onCreate({...sleepSession});
    }

    const formDataValid = true;

    return (
        <>
            <Button variant="success" onClick={() => setShowLogSleep(true)}>Log Sleep</Button>

            <Modal show={showLogSleep} onHide={onHide} >
                <Modal.Header closeButton>
                    <Modal.Title>Log Sleep</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="mb-3">
                        <label htmlFor="dateStart" className="form-label">Sleep Session Start</label>
                        <DatePicker
                            className="form-control" id="dateStart" placeholder="Date Start"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={sleepSession.startDate}
                            onChange={ date => updateSleepSession({startDate : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="dateEnd" className="form-label">Sleep Session End</label>
                        <DatePicker
                            className="form-control" id="dateEnd" placeholder="Date End"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={sleepSession.endDate}
                            onChange={ date => updateSleepSession({endDate : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="outOfBed" className="form-label">Out Of Bed (number of times)</label>
                        <input type="text"  className="form-control" id="outOfBed" placeholder="Out of Bed (number of times)"
                               value={sleepSession.outOfBed}
                               onChange={e => updateSleepSession({outOfBed : e.target.value })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea className="form-control" id="notes" placeholder="Notes"
                               value={sleepSession.notes}
                               onChange={e => updateSleepSession({notes : e.target.value })} />
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>Close</Button>
                    <Button variant="primary" onClick={onConfirm} disabled={!formDataValid}>Save</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default CreateSleepSession;
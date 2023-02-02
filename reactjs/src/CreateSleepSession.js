import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';


const numericRegex=/^[0-9]+$/;

const minutesBetween = (date1, date2) => {

    if (typeof date1 === 'string' || date1 instanceof String) {
        date1 = new Date(date1);
    }

    if (typeof date2 === 'string' || date2 instanceof String) {
        date2 = new Date(date2);
    }

    let diff = (date2.getTime() - date1.getTime()) / 1000;
    diff /= 60;
    return Math.abs(Math.round(diff));
}

const minuteToHrMin = (minutes) => {

    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

function toIsoString(date) {

    const tzo = -date.getTimezoneOffset(),
        dif = tzo >= 0 ? '+' : '-',
        pad = function(num) {
            return (num < 10 ? '0' : '') + num;
        };

    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate()) +
        'T' + pad(date.getHours()) +
        ':' + pad(date.getMinutes()) +
        ':' + pad(date.getSeconds()) +
        dif + pad(Math.floor(Math.abs(tzo) / 60)) +
        ':' + pad(Math.abs(tzo) % 60);
}

function CreateSleepSession(props) {


    const {currentUser} = useCurrentUser();


    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    const initSleepData = {
        startTime: yesterday,
        stopTime: today,
        notes: '',
        outOfBed: 0,
        tags: []
    }

    const sleepUrl = '/user/' + currentUser.username + '/sleep';


    const post = useApiPost();
    const [showLogSleep, setShowLogSleep] = useState(false);
    const [sleepData, setSleepData] = useState(initSleepData);

    const onCreate = () => {

        console.log(sleepData);

        const formattedSleepData = {
            notes: sleepData.notes,
            outOfBed: sleepData.outOfBed,
            tags: sleepData.tags,
            startTime: toIsoString(sleepData.startTime),
            stopTime: toIsoString(sleepData.stopTime)
        }

        post(sleepUrl, JSON.stringify(formattedSleepData))
            .then(result => setShowLogSleep(false))
            .then(props.onSave);
    }

    function updateSleepSession(updateValues) {

        let updatedSleep = {...sleepData, ...updateValues};

        if( numericRegex.test(updatedSleep.outOfBed)) {
            setSleepData( updatedSleep );
        }
    }

    function onHide() {
        setSleepData(initSleepData);
        setShowLogSleep(false);
    }

    function onConfirm() {
        onCreate();
        setSleepData(initSleepData);
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
                        <label htmlFor="startTime" className="form-label">Sleep Session Start</label>
                        <DatePicker
                            className="form-control" id="startTime" placeholder="Date Start"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={sleepData.startTime}
                            onChange={ date => updateSleepSession({startTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="stopTime" className="form-label">Sleep Session End</label>
                        <DatePicker
                            className="form-control" id="stopTime" placeholder="Date End"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={sleepData.stopTime}
                            onChange={ date => updateSleepSession({stopTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="calculatedMinutes" className="form-label">Time Asleep</label>
                        <input disabled className="form-control" id="calculatedMinutes" placeholder="Time Asleep"
                               value={minuteToHrMin(minutesBetween(sleepData.startTime, sleepData.stopTime))} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="outOfBed" className="form-label">Out Of Bed (number of times)</label>
                        <input type="text"  className="form-control" id="outOfBed" placeholder="Out of Bed (number of times)"
                               value={sleepData.outOfBed}
                               onChange={e => updateSleepSession({outOfBed : e.target.value})} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea className="form-control" id="notes" placeholder="Notes"
                               value={sleepData.notes}
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

export {CreateSleepSession, minuteToHrMin, minutesBetween};

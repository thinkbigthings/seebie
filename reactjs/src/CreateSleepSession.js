import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";


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

function createInitSleepData() {

    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    return {
        startTime: yesterday,
        stopTime: today,
        notes: '',
        outOfBed: 0,
        tags: [],
    }
}

function format(sleepData) {
    const formattedSleepData = {
        notes: sleepData.notes,
        outOfBed: sleepData.outOfBed,
        tags: sleepData.tags,
        startTime: toIsoString(sleepData.startTime),
        stopTime: toIsoString(sleepData.stopTime)
    }
    return JSON.stringify(formattedSleepData);
}

function CreateSleepSession(props) {

    const initSleepData = createInitSleepData();

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const post = useApiPost();
    const [showLogSleep, setShowLogSleep] = useState(false);

    const saveData = (data) => {
        post(sleepUrl, format(data))
            .then(result => setShowLogSleep(false))
            .then(props.onSave);
    }

    function hideModal() {
        setShowLogSleep(false);
    }

    return (
        <>
            <Button variant="success" onClick={() => setShowLogSleep(true)}>Log Sleep</Button>

            <Modal show={showLogSleep} onHide={hideModal} >
                <Modal.Header closeButton>
                    <Modal.Title>Log Sleep</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <SleepForm onCancel={hideModal} onSave={saveData} initData={initSleepData} />
                </Modal.Body>
            </Modal>
        </>
    );
}

export {CreateSleepSession, minuteToHrMin, minutesBetween};

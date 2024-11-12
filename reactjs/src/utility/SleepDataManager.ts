import {SleepData} from "../types/sleep.types";

const pad = function(num: number) {
    return (num < 10 ? '0' : '') + num;
};

const toIsoLocalDate = (date: Date) => {

    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate());
}

const toIsoString = (date: Date) => {

    const tzo = -date.getTimezoneOffset();
    const dif = tzo >= 0 ? '+' : '-';

    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate()) +
        'T' + pad(date.getHours()) +
        ':' + pad(date.getMinutes()) +
        ':' + pad(date.getSeconds()) +
        dif + pad(Math.floor(Math.abs(tzo) / 60)) +
        ':' + pad(Math.abs(tzo) % 60);
}

const createInitSleepData = ():SleepData => {

    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    // leave the startTime and stopTime as strings
    return {
        id: 0,
        localStartTime: yesterday,
        localStopTime: today,
        startTime: toIsoString(yesterday),
        stopTime: toIsoString(today),
        notes: '',
        minutesAwake: 0,
        minutesAsleep: (today.getTime() - yesterday.getTime()) / 60000,
        zoneId: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
}
export {createInitSleepData, toIsoString, toIsoLocalDate}

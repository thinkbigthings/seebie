import {SleepData} from "../types/sleep.types";
import {Duration, LocalDateTime} from "@js-joda/core";

const pad = function(num: number) {
    return (num < 10 ? '0' : '') + num;
};

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

    let today = LocalDateTime.now();
    let yesterday = today.minusHours(8);

    // leave the startTime and stopTime as strings
    return {
        id: 0,
        startTime: yesterday,
        stopTime: today,
        notes: '',
        minutesAwake: 0,
        minutesAsleep: Duration.between(yesterday, today).toMinutes(),
        zoneId: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
}
export {createInitSleepData, toIsoString}

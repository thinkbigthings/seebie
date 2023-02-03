

// https://stackoverflow.com/questions/122102/what-is-the-most-efficient-way-to-deep-clone-an-object-in-javascript/10916838

// Can also use Lodash to copy, it has a ton of other utilities too

import copy from "./Copier";

const toIsoString = (date) => {

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



const SleepDataManager = { };

SleepDataManager.parse = (json) => {
    let parsed = copy(json);
    parsed.startTime = new Date(Date.parse(json.startTime));
    parsed.stopTime = new Date(Date.parse(json.stopTime));
    return parsed;
}

SleepDataManager.format = (sleepData) => {

    const formattedSleepData = {
        notes: sleepData.notes,
        outOfBed: sleepData.outOfBed,
        tags: sleepData.tags,
        startTime: toIsoString(sleepData.startTime),
        stopTime: toIsoString(sleepData.stopTime)
    }
    return JSON.stringify(formattedSleepData);
}

SleepDataManager.createInitSleepData = () => {

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

const minutesBetween = (date1, date2) => {

    // if (typeof date1 === 'string' || date1 instanceof String) {
    //     date1 = new Date(date1);
    // }
    //
    // if (typeof date2 === 'string' || date2 instanceof String) {
    //     date2 = new Date(date2);
    // }

    let diff = (date2.getTime() - date1.getTime()) / 1000;
    diff /= 60;
    return Math.abs(Math.round(diff));
}

const minuteToHrMin = (minutes) => {

    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

SleepDataManager.isDataValid = (sleepData) => {
    const numericRegex=/^[0-9]+$/;
    if( numericRegex.test(sleepData.outOfBed)) {
        return true;
    }
    return false;
}

SleepDataManager.formatDuration = (date1, date2) => {
    return minuteToHrMin(minutesBetween(date1, date2));
}

export default SleepDataManager;

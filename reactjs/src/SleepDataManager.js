

// https://stackoverflow.com/questions/122102/what-is-the-most-efficient-way-to-deep-clone-an-object-in-javascript/10916838

// Can also use Lodash to copy, it has a ton of other utilities too

import copy from "./Copier";



const SleepDataManager = { };

SleepDataManager.toIsoString = (date) => {

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

SleepDataManager.parse = (json) => {
    let parsed = copy(json);
    parsed.startTime = new Date(Date.parse(json.startTime));
    parsed.stopTime = new Date(Date.parse(json.stopTime));
    return parsed;
}

SleepDataManager.format = (sleepData) => {

    const formattedSleepData = copy(sleepData);

    formattedSleepData.startTime = SleepDataManager.toIsoString(sleepData.startTime);
    formattedSleepData.stopTime = SleepDataManager.toIsoString(sleepData.stopTime);

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
        minutesAwake: 0,
        tags: [],
        zoneId: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
}

SleepDataManager.minuteToHrMin = (minutes) => {

    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

SleepDataManager.isDataValid = (sleepData) => {
    const numericRegex=/^[0-9]+$/;
    if( numericRegex.test(sleepData.minutesAwake)) {
        return true;
    }
    return false;
}

export default SleepDataManager;

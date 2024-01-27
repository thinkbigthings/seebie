
const SleepDataManager = { };

SleepDataManager.toIsoLocalDate = (date) => {

    const tzo = -date.getTimezoneOffset();
    const dif = tzo >= 0 ? '+' : '-';
    const pad = function(num) {
        return (num < 10 ? '0' : '') + num;
    };

    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate());
}

SleepDataManager.toIsoString = (date) => {

    const tzo = -date.getTimezoneOffset();
    const dif = tzo >= 0 ? '+' : '-';
    const pad = function(num) {
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
    let parsed = structuredClone(json);
    // keep the local time without the offset for display purposes
    parsed.localStartTime = new Date(Date.parse(json.startTime.substring(0, 19)));
    parsed.localStopTime = new Date(Date.parse(json.stopTime.substring(0, 19)));
    // leave the original startTime and stopTime as strings
    // parsed.startTime = new Date(Date.parse(json.startTime));
    // parsed.stopTime = new Date(Date.parse(json.stopTime));
    return parsed;
}

SleepDataManager.format = (sleepData) => {

    const formattedSleepData = structuredClone(sleepData);

    return JSON.stringify(formattedSleepData);
}

SleepDataManager.createInitSleepData = () => {

    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    // leave the startTime and stopTime as strings
    return {
        localStartTime: yesterday,
        localStopTime: today,
        startTime: SleepDataManager.toIsoString(yesterday),
        stopTime: SleepDataManager.toIsoString(today),
        notes: '',
        minutesAwake: 0,
        zoneId: Intl.DateTimeFormat().resolvedOptions().timeZone
    }
}

SleepDataManager.minuteToHrMin = (minutes) => {

    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

export default SleepDataManager;

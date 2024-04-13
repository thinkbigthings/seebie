
interface SleepData {
    localStartTime: Date;
    localStopTime: Date;
    startTime: string;
    stopTime: string;
    notes: string;
    minutesAwake: number;
    zoneId: string;
}

const SleepDataManager = {

    toIsoLocalDate: (date: Date) => {

        const tzo = -date.getTimezoneOffset();
        const dif = tzo >= 0 ? '+' : '-';
        const pad = function(num: number) {
            return (num < 10 ? '0' : '') + num;
        };

        return date.getFullYear() +
            '-' + pad(date.getMonth() + 1) +
            '-' + pad(date.getDate());
    },

    toIsoString: (date: Date) => {

        const tzo = -date.getTimezoneOffset();
        const dif = tzo >= 0 ? '+' : '-';
        const pad = function(num: number) {
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
    },

    minuteToHrMin: (minutes: number) => {

        const hr = Math.floor(minutes / 60);
        const m = minutes % 60;
        return hr + 'hr ' + m + 'm';
    },

    createInitSleepData: ():SleepData => {

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
    },

    format: (sleepData: SleepData):string => {
        const formattedSleepData = structuredClone(sleepData);
        return JSON.stringify(formattedSleepData);
    },

    parse: (json: SleepData) => {
        let parsed = structuredClone(json);
        // keep the local time without the offset for display purposes
        parsed.localStartTime = new Date(Date.parse(json.startTime.substring(0, 19)));
        parsed.localStopTime = new Date(Date.parse(json.stopTime.substring(0, 19)));
        // leave the original startTime and stopTime as strings
        // parsed.startTime = new Date(Date.parse(json.startTime));
        // parsed.stopTime = new Date(Date.parse(json.stopTime));
        return parsed;
    }
};

export default SleepDataManager;

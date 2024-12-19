import {SleepData} from "../types/sleep.types";
import {Duration, LocalDateTime} from "@js-joda/core";

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
export {createInitSleepData}

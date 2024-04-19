
// this is the representation used internally by the front end
// if id===0 then it is unsaved
interface SleepData {
    id: number,
    notes: string,
    minutesAwake: number,
    minutesAsleep: number,
    startTime: string,
    stopTime: string,
    zoneId: string,
    localStartTime: Date,
    localStopTime: Date
}

// this is what we send back and forth with the server
interface SleepDto {
    notes: string,
    minutesAwake: number,
    startTime: string,
    stopTime: string,
    zoneId: string
}

// this is what we send back and forth with the server
interface SleepDetailDto {
    id: number,
    minutesAsleep: number,
    sleepData: SleepDto
}

export type { SleepData, SleepDto, SleepDetailDto }


// this is the representation used internally by the front end
// if id===0 then it is unsaved
import {LocalDate, LocalDateTime} from "@js-joda/core";

// this is the representation used internally by the front end
interface SleepData {
    id: number,
    notes: string,
    minutesAwake: number,
    minutesAsleep: number,
    startTime: LocalDateTime,
    stopTime: LocalDateTime,
    zoneId: string,
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

interface DateRange {
    from: LocalDate,
    to: LocalDate
}

export type { SleepData, SleepDto, SleepDetailDto, DateRange}

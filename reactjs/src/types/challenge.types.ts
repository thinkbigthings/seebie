import {LocalDate} from "@js-joda/core";

// this is the representation used internally by the front end
interface ChallengeData {
    id: number, // if id===0 then it is unsaved
    name: string,
    description: string,
    start: LocalDate,
    finish: LocalDate,
    localStartTime: Date,
    localEndTime: Date
    exactStart: Date,
    exactFinish: Date
}

// this is what we send back and forth with the server
interface ChallengeDto {
    name: string,
    description: string,
    start: string,
    finish: string
}

// this is what we send back and forth with the server
interface ChallengeDetailDto {
    id: number,
    challenge: ChallengeDto
}

interface ChallengeList<T> {
    current: T[];
    upcoming: T[];
    completed: T[];
}

export type { ChallengeData, ChallengeDto, ChallengeDetailDto, ChallengeList }
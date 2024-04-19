
// this is the representation used internally by the front end
// if id===0 then it is unsaved
interface ChallengeData {
    id: number,
    name: string,
    description: string,
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
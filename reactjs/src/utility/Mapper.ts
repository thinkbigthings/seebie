import SleepDataManager from "./SleepDataManager";

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

const toSelectableChallenges = (challengeList: ChallengeList<ChallengeData>, defaultChallenge: ChallengeData) => {

    let selectableChallenges = [...challengeList.current, ...challengeList.completed];

    // if user already has a challenge with the same name as the default challenge,
    // keep the user's challenge and don't provide the default challenge
    if ( ! selectableChallenges.some(challenge => challenge.name === defaultChallenge.name)) {
        selectableChallenges.push(defaultChallenge);
    }

    return selectableChallenges;
}

const toLocalChallengeDataList = (challengeList: ChallengeList<ChallengeDetailDto>) : ChallengeList<ChallengeData> => {
    return {
        current: challengeList.current.map(toLocalChallengeData),
        upcoming: challengeList.upcoming.map(toLocalChallengeData),
        completed: challengeList.completed.map(toLocalChallengeData)
    };
}

const toChallengeDetailDto = (dto:ChallengeDto, id:number): ChallengeDetailDto => {
    return {
        id: id,
        challenge: dto
    }
}

const toLocalChallengeData = (challengeDetails: ChallengeDetailDto): ChallengeData => {

    const challenge: ChallengeDto = challengeDetails.challenge;

    let exactStart = new Date(challenge.start);
    let exactFinish = new Date(challenge.finish);
    exactStart.setHours(0, 0, 0);
    exactFinish.setHours(23, 59, 59);

    return {
        id: challengeDetails.id,
        name: challenge.name,
        description: challenge.description,
        localStartTime: new Date(challenge.start),
        localEndTime: new Date(challenge.finish),
        exactStart: exactStart,
        exactFinish: exactFinish
    }
}

const toChallengeDto = (challenge: ChallengeData): ChallengeDto => {
    return {
        name: challenge.name,
        description: challenge.description,
        start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
        finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
    }
}

export {toSelectableChallenges, toChallengeDto, toLocalChallengeData, toLocalChallengeDataList, toChallengeDetailDto}
export type {ChallengeDto, ChallengeDetailDto, ChallengeData, ChallengeList}
import SleepDataManager from "./SleepDataManager";
import ChallengeForm from "../ChallengeForm.tsx";

interface ChallengeFormData {
    name: string,
    description: string,
    localStartTime: Date,
    localEndTime: Date
    exactStart: Date,
    exactFinish: Date
}

interface ChallengeDto {
    name: string,
    description: string,
    start: string,
    finish: string
}

interface ChallengeDetailDto {
    id: number,
    challenge: ChallengeDto
}

interface ChallengeList<T> {
    current: T[];
    upcoming: T[];
    completed: T[];
}

const toSelectableChallenges = (challengeList: ChallengeList<ChallengeFormData>, defaultChallenge: ChallengeFormData) => {

    let selectableChallenges = [...challengeList.current, ...challengeList.completed];

    // if user already has a challenge with the same name as the default challenge,
    // keep the user's challenge and don't provide the default challenge
    if ( ! selectableChallenges.some(challenge => challenge.name === defaultChallenge.name)) {
        selectableChallenges.push(defaultChallenge);
    }

    return selectableChallenges;
}

const extractChallenges = (challengeList: ChallengeList<ChallengeDetailDto>) : ChallengeList<ChallengeDto> => {
    return {
        current: challengeList.current.map(details => details.challenge),
        upcoming: challengeList.upcoming.map(details => details.challenge),
        completed: challengeList.completed.map(details => details.challenge)
    };
}

const fromChallengeDtoList = (challengeList: ChallengeList<ChallengeDto>) : ChallengeList<ChallengeFormData> => {
    return {
        current: challengeList.current.map(fromChallengeDto),
        upcoming: challengeList.upcoming.map(fromChallengeDto),
        completed: challengeList.completed.map(fromChallengeDto)
    };
}

const fromChallengeDto = (challenge: ChallengeDto): ChallengeFormData => {

    let exactStart = new Date(challenge.start);
    let exactFinish = new Date(challenge.finish);
    exactStart.setHours(0, 0, 0);
    exactFinish.setHours(23, 59, 59);

    return {
        name: challenge.name,
        description: challenge.description,
        localStartTime: new Date(challenge.start),
        localEndTime: new Date(challenge.finish),
        exactStart: exactStart,
        exactFinish: exactFinish
    }
}

const toChallengeDto = (challenge: ChallengeFormData): ChallengeDto => {
    return {
        name: challenge.name,
        description: challenge.description,
        start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
        finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
    }
}

export {toSelectableChallenges, toChallengeDto, fromChallengeDto, extractChallenges, fromChallengeDtoList}
export type {ChallengeDto, ChallengeDetailDto, ChallengeFormData, ChallengeList}
import {toIsoLocalDate, toIsoString} from "./SleepDataManager";
import {SleepData, SleepDetailDto, SleepDto} from "../types/sleep.types";
import {ChallengeDto, ChallengeDetailDto, ChallengeList, ChallengeData} from "../types/challenge.types";

const toSelectableChallenges = (challengeList: ChallengeList<ChallengeData>, defaultChallenge: ChallengeData) => {

    let selectableChallenges = [...challengeList.current, ...challengeList.completed];

    // if user already has a challenge with the same name as the default challenge,
    // keep the user's challenge and don't provide the default challenge
    if (!selectableChallenges.some(challenge => challenge.name === defaultChallenge.name)) {
        selectableChallenges.push(defaultChallenge);
    }

    return selectableChallenges;
}

const toLocalChallengeDataList = (challengeList: ChallengeList<ChallengeDetailDto>): ChallengeList<ChallengeData> => {
    return {
        current: challengeList.current.map(toLocalChallengeData),
        upcoming: challengeList.upcoming.map(toLocalChallengeData),
        completed: challengeList.completed.map(toLocalChallengeData)
    };
}

const toChallengeDetailDto = (dto: ChallengeDto, id: number): ChallengeDetailDto => {
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
        start: toIsoLocalDate(challenge.localStartTime),
        finish: toIsoLocalDate(challenge.localEndTime)
    }
}

const toLocalSleepData = (details: SleepDetailDto): SleepData => {
    return {
        id: details.id,
        notes: details.sleepData.notes,
        minutesAwake: details.sleepData.minutesAwake,
        minutesAsleep: details.minutesAsleep,
        startTime: details.sleepData.startTime,
        stopTime: details.sleepData.stopTime,
        zoneId: details.sleepData.zoneId,
        // keep the local time without the offset for display purposes
        localStartTime: new Date(Date.parse(details.sleepData.startTime.substring(0, 19))),
        localStopTime: new Date(Date.parse(details.sleepData.stopTime.substring(0, 19))),
    }
}

const toSleepDto = (sleep: SleepData): SleepDto => {

    // use the local time without the offset for display purposes
    let localStartTime = toIsoString(sleep.localStartTime).substring(0, 19);
    let localStopTime = toIsoString(sleep.localStopTime).substring(0, 19);

    return {
        notes: sleep.notes,
        minutesAwake: sleep.minutesAwake,
        startTime: localStartTime + sleep.startTime.substring(19),
        stopTime: localStopTime + sleep.stopTime.substring(19),
        zoneId: sleep.zoneId
    }
}

export {
    toSelectableChallenges, toChallengeDto, toLocalChallengeData, toLocalChallengeDataList, toChallengeDetailDto,
    toLocalSleepData, toSleepDto
}

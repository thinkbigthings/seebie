import {toIsoString} from "./SleepDataManager";
import {SleepData, SleepDetailDto, SleepDto} from "../types/sleep.types";
import {ChallengeDto, ChallengeDetailDto, ChallengeList, ChallengeData} from "../types/challenge.types";

import {LocalDate} from "@js-joda/core"

const toLocalDate = (date: Date) => {
    return LocalDate.of(date.getFullYear(), date.getMonth()+1, date.getDate());
}

const toDate = (date: LocalDate) => {
    return new Date(date.year(), date.monthValue()-1, date.dayOfMonth());
}

const toSelectableChallenges = (challengeList: ChallengeList<ChallengeData>, defaultChallenge: ChallengeData) => {

    let selectableChallenges = [...challengeList.current, ...challengeList.completed];

    // if user already has a challenge with the same name as the default challenge,
    // keep the user's challenge and don't provide the default challenge
    if (!selectableChallenges.some(challenge => challenge.name === defaultChallenge.name)) {
        selectableChallenges.push(defaultChallenge);
    }

    return selectableChallenges;
}

function calculateProgress(challenge: ChallengeData): number {

    const now = LocalDate.now();
    const { start, finish } = challenge;

    if(now.isAfter(finish)) {
        return 100;
    }
    if(now.isBefore(start)) {
        return 0;
    }

    const totalDuration = start.until(finish).days();
    const elapsedDuration = start.until(now).days();

    return Math.round((elapsedDuration / totalDuration) * 100);
}

const toLocalChallengeDataList = (challengeList: ChallengeDetailDto[]): ChallengeData[] => {
    return challengeList.map(toLocalChallengeData);
}


const toChallengeList = (challengeList: ChallengeDetailDto[]): ChallengeList<ChallengeData> => {

    const challengeData = challengeList.map(toLocalChallengeData);
    const now = LocalDate.now();

    // If the start date or end date is today, it is current because it's what the user is looking for

    return {
        current: challengeData.filter(c => c.start.compareTo(now) <= 0 && now.compareTo(c.finish) <= 0),
        upcoming: challengeData.filter(c => c.start.compareTo(now) > 0),
        completed: challengeData.filter(c => now.compareTo(c.finish) > 0)
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

    return {
        id: challengeDetails.id,
        name: challenge.name,
        description: challenge.description,
        start: LocalDate.parse(challenge.start),
        finish: LocalDate.parse(challenge.finish),
    }
}

const toChallengeDto = (challenge: ChallengeData): ChallengeDto => {
    return {
        name: challenge.name,
        description: challenge.description,
        start: challenge.start.toString(),
        finish: challenge.finish.toString()
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
    toLocalSleepData, toSleepDto, calculateProgress, toChallengeList, toLocalDate, toDate
}

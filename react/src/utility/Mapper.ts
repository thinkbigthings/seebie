import {SleepData, SleepDetailDto, SleepDto} from "../types/sleep.types";
import {ChallengeDto, ChallengeDetailDto, ChallengeList, ChallengeData} from "../types/challenge.types";

import {ChronoUnit, convert, DateTimeFormatter, LocalDate, LocalDateTime, nativeJs} from "@js-joda/core"

function ensure<T>(argument: T | undefined | null, message: string = 'This value was promised to be there.'): T {
    if (argument === undefined || argument === null) {
        throw new TypeError(message);
    }
    return argument as T;
}

const localDateTimeToString = (date: LocalDateTime): string => {
    return date.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
}

const localDateToString = (date: LocalDate): string => {
    return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
}

const jsDateToLocalDate = (date: Date): LocalDate => {
    return nativeJs(date).toLocalDate();
}

const localDateToJsDate = (date: LocalDate): Date => {
    return convert(date).toDate();
}

const jsDateToLocalDateTime = (date: Date): LocalDateTime => {
    return nativeJs(date).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
}

const localDateTimeToJsDate = (date: LocalDateTime): Date => {
    return convert(date).toDate();
}

const toSelectableChallenges = (challengeList: ChallengeList, defaultChallenge: ChallengeData) => {

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

const toChallengeList = (challengeList: ChallengeDetailDto[]): ChallengeList => {

    const challengeData = challengeList.map(toLocalChallengeData);
    const now = LocalDate.now();

    // If the start date or end date is today, it is current because it's what the user is looking for

    return {
        current: challengeData.filter(c => c.start.compareTo(now) <= 0 && now.compareTo(c.finish) <= 0),
        upcoming: challengeData.filter(c => c.start.compareTo(now) > 0),
        completed: challengeData.filter(c => now.compareTo(c.finish) > 0)
    };
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
        start: localDateToString(challenge.start),
        finish: localDateToString(challenge.finish)
    }
}

const toLocalSleepData = (details: SleepDetailDto): SleepData => {
    return {
        id: details.id,
        notes: details.sleepData.notes,
        minutesAwake: details.sleepData.minutesAwake,
        minutesAsleep: details.minutesAsleep,
        startTime: LocalDateTime.parse(details.sleepData.startTime),
        stopTime: LocalDateTime.parse(details.sleepData.stopTime),
        zoneId: details.sleepData.zoneId,
    }
}

const toSleepDto = (sleep: SleepData): SleepDto => {

    return {
        notes: sleep.notes,
        minutesAwake: sleep.minutesAwake,
        startTime: localDateTimeToString(sleep.startTime),
        stopTime: localDateTimeToString(sleep.stopTime),
        zoneId: sleep.zoneId
    }
}

export {
    ensure, toSelectableChallenges, toChallengeDto, toLocalChallengeData,
    toLocalSleepData, toSleepDto, calculateProgress, toChallengeList, jsDateToLocalDate, localDateToJsDate, jsDateToLocalDateTime,
    localDateTimeToJsDate, localDateToString
}

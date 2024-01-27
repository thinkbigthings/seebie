import SleepDataManager from "./SleepDataManager";

// incoming list contains challenge not challengeDetails
const toSelectableChallenges = (challengeList, defaultChallenge) => {

    let selectableChallenges = [...challengeList.current, ...challengeList.completed];

    // if user already has a challenge with the same name as the default challenge,
    // keep the user's challenge and don't provide the default challenge
    if ( ! selectableChallenges.some(challenge => challenge.name === defaultChallenge.name)) {
        selectableChallenges.push(defaultChallenge);
    }

    return selectableChallenges;
}

const mapChallengeDetails = (challengeList, mapFunction) => {

    let newChallengeList = {};

    newChallengeList.current = challengeList.current.map(mapFunction);
    newChallengeList.upcoming = challengeList.upcoming.map(mapFunction);
    newChallengeList.completed = challengeList.completed.map(mapFunction);

    return newChallengeList;
}

const withExactTime = (challengeDetail) => {
    let exactStart = new Date(challengeDetail.challenge.start);
    let exactFinish = new Date(challengeDetail.challenge.finish);
    exactStart.setHours(0, 0, 0);
    exactFinish.setHours(23, 59, 59);

    return {
        ...challengeDetail,
        challenge: {
            ...challengeDetail.challenge,
            exactStart: exactStart,
            exactFinish: exactFinish
        }
    };
};

const fromChallengeDto = (challenge) => {
    return {
        name: challenge.name,
        description: challenge.description,
        localStartTime: new Date(challenge.start),
        localEndTime: new Date(challenge.finish)
    }
}

const toChallengeDto = (challenge) => {
    return {
        name: challenge.name,
        description: challenge.description,
        start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
        finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
    }
}

export {withExactTime, toSelectableChallenges, toChallengeDto, fromChallengeDto, mapChallengeDetails}
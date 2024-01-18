import SleepDataManager from "../SleepDataManager";

const mapChallengeDetails = (challengeList, mapFunction) => {
    let flatChallengeList = {};
    flatChallengeList.current = challengeList.current.map(mapFunction);
    flatChallengeList.upcoming = challengeList.upcoming.map(mapFunction);
    flatChallengeList.completed = challengeList.completed.map(mapFunction);
    return flatChallengeList;
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

const toChallengeDto = (challenge) =>{
    return {
        name: challenge.name,
        description: challenge.description,
        start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
        finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
    }
}

export {withExactTime, toChallengeDto, mapChallengeDetails}
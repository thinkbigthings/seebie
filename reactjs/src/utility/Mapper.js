import SleepDataManager from "../SleepDataManager";

const withExactTimes = (challengeList) => {
    let exactChallengeList = {};
    exactChallengeList.current = challengeList.current.map(withExactTime);
    exactChallengeList.upcoming = challengeList.upcoming.map(withExactTime);
    exactChallengeList.completed = challengeList.completed.map(withExactTime);
    return exactChallengeList;
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

export {withExactTimes, toChallengeDto}
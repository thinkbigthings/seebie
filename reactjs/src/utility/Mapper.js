import SleepDataManager from "../SleepDataManager";

const withExactTimes = (challengeList) => {
    let exactChallengeList = {};
    exactChallengeList.current = challengeList.current.map(withExactTime);
    exactChallengeList.upcoming = challengeList.upcoming.map(withExactTime);
    exactChallengeList.completed = challengeList.completed.map(withExactTime);
    return exactChallengeList;
}

const withExactTime = (challenge) => {
    let exactStart = new Date(challenge.start);
    let exactFinish = new Date(challenge.finish);
    exactStart.setHours(0, 0, 0);
    exactFinish.setHours(23, 59, 59);
    return {
        name: challenge.name,
        description: challenge.description,
        start: challenge.start,
        finish: challenge.finish,
        exactStart: exactStart,
        exactFinish: exactFinish
    }
}

const toChallengeDto = (challenge) =>{
    return {
        name: challenge.name,
        description: challenge.description,
        start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
        finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
    }
}

export {withExactTimes, toChallengeDto}
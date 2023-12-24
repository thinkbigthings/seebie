import CollapsibleContent from "./CollapsibleContent";
import React from "react";

function calculateProgress(start, now, end) {

    const totalDuration = end - start;
    const durationFromStartToNow = now - start;
    const effectiveDuration = Math.min(durationFromStartToNow, totalDuration);

    return Math.round((effectiveDuration / totalDuration) * 100);
}

function CollapsibleChallenge(props) {

    const {challenge} = props;

    const startDate = new Date(challenge.start);
    const finishDate = new Date(challenge.finish);
    const now = new Date();

    const inProgress = startDate < now && now < finishDate;
    const progress = inProgress ? calculateProgress(startDate, now, finishDate) : -1;

    return (
        <CollapsibleContent title={challenge.name}>
            <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
            <div className={"fw-bold"}>Start: {challenge.start}</div>
            <div className={"fw-bold"}>Finish: {challenge.finish}</div>
            {inProgress
                ? <div className="progress my-2" role="progressbar" style={{height: "25px"}}
                           aria-label="Basic example" aria-valuenow={progress} aria-valuemin="0" aria-valuemax="100">
                        <div className="progress-bar btn-secondary" style={{width: progress + "%"}}>{progress + "%"}</div>
                  </div>
                : <span />
            }
        </CollapsibleContent>
    );
}

export default CollapsibleChallenge;

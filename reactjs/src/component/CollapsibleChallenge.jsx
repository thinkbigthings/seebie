import CollapsibleContent from "./CollapsibleContent";
import React from "react";

function calculateProgress(start, now, end) {

    const totalDuration = end - start;
    const durationFromStartToNow = now - start;
    const effectiveDuration = Math.min(durationFromStartToNow, totalDuration);

    return Math.round((effectiveDuration / totalDuration) * 100);
}

const localeOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
};

const locale = "en-US";

function CollapsibleChallenge(props) {

    const {challenge} = props;

    const startDate = new Date(challenge.challenge.start);
    const finishDate = new Date(challenge.challenge.finish);
    const now = new Date();

    const inProgress = startDate < now && now < finishDate;
    const progress = inProgress ? calculateProgress(startDate, now, finishDate) : -1;

    const formattedStart = startDate.toLocaleDateString(locale, localeOptions);
    const formattedFinish = finishDate.toLocaleDateString(locale, localeOptions);

    return (
        <CollapsibleContent title={challenge.challenge.name}>
            <div className={"mb-2 pb-2 border-bottom"}>{challenge.challenge.description}</div>
            <div className={"fw-bold"}>{formattedStart} --- {formattedFinish}</div>
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

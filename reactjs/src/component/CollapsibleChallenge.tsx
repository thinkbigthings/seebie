import CollapsibleContent from "./CollapsibleContent";
import React from "react";
import WarningButton from "./WarningButton";
import {Link, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import {ChallengeData} from "../types/challenge.types";

function calculateProgress(start:Date, now:Date, end:Date) {

    const totalDuration = end.getTime() - start.getTime();
    const durationFromStartToNow = now.getTime() - start.getTime();
    const effectiveDuration = Math.min(durationFromStartToNow, totalDuration);

    return Math.round((effectiveDuration / totalDuration) * 100);
}

const localeOptions:Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
};

const locale = "en-US";

function CollapsibleChallenge(props:{challenge:ChallengeData, onDelete:()=>void}) {

    const {challenge, onDelete} = props;

    const {username} = useParams();

    const startDate = challenge.localStartTime;
    const finishDate = challenge.localEndTime;
    const now = new Date();

    const inProgress = startDate < now && now < finishDate;
    const progress = inProgress ? calculateProgress(startDate, now, finishDate) : -1;

    const formattedStart = startDate.toLocaleDateString(locale, localeOptions);
    const formattedFinish = finishDate.toLocaleDateString(locale, localeOptions);

    return (
        <CollapsibleContent title={challenge.name}>
            <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
            <div className={"fw-bold"}>{formattedStart} --- {formattedFinish}</div>
            {inProgress
                ? <div className="progress my-2" role="progressbar" style={{height: "25px"}}
                           aria-label="Basic example" aria-valuenow={progress} aria-valuemin={0} aria-valuemax={100}>
                        <div className="progress-bar btn-secondary" style={{width: progress + "%"}}>{progress + "%"}</div>
                  </div>
                : <span />
            }
            <div className="d-flex justify-content-end">
                <Link to={`/users/${username}/challenge/${challenge.id}/edit` } >
                    <Button variant={"secondary"} className="me-2" >
                        <FontAwesomeIcon className="me-2" icon={faEdit} />Edit
                    </Button>
                </Link>
                <WarningButton buttonText="Delete" onConfirm={onDelete}>
                    Are you sure you want to delete this challenge? This cannot be undone.
                </WarningButton>
            </div>
        </CollapsibleContent>
    );
}

export default CollapsibleChallenge;

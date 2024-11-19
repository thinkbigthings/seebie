import CollapsibleContent from "./CollapsibleContent";
import React from "react";
import WarningButton from "./WarningButton";
import {Link, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import {ChallengeData} from "../types/challenge.types";
import {ChronoUnit, LocalDate} from "@js-joda/core";

function calculateProgress(start:LocalDate, now:LocalDate, end:LocalDate) {

    if(now.isBefore(start)) {
        return 0;
    }
    if(now.isAfter(end)) {
        return 100;
    }

    const startToNow = start.until(now).days();
    const startToEnd = start.until(end).days();
    return Math.round(startToNow * 100 / startToEnd);
}

function CollapsibleChallenge(props:{challenge:ChallengeData, onDelete:()=>void}) {

    const {challenge, onDelete} = props;

    const {username} = useParams();

    const startDate = challenge.start;
    const finishDate = challenge.finish;
    const now = LocalDate.now();

    const progress = calculateProgress(startDate, now, finishDate);
    const inProgress = 0 < progress && progress < 100;

    const formattedStart = startDate.toString()
    const formattedFinish = finishDate.toString();

    return (
        <CollapsibleContent title={challenge.name}>
            <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
            <div className={"fw-bold"}>{formattedStart} --- {formattedFinish}</div>
                <div className="progress my-2" role="progressbar" style={{height: "25px"}}
                       aria-label="Basic example" aria-valuenow={progress} aria-valuemin={0} aria-valuemax={100}>
                    <div className="progress-bar btn-secondary" style={{width: progress + "%"}}>{progress + "%"}</div>
                </div>
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

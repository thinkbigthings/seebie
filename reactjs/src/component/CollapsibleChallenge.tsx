import CollapsibleContent from "./CollapsibleContent";
import React from "react";
import WarningButton from "./WarningButton";
import {Link, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit} from "@fortawesome/free-solid-svg-icons";
import Button from "react-bootstrap/Button";
import {ChallengeData} from "../types/challenge.types";
import {calculateProgress} from "../utility/Mapper.ts";


function CollapsibleChallenge(props:{challenge:ChallengeData, onDelete:()=>void}) {

    const {challenge, onDelete} = props;

    const {username} = useParams();

    const progress = calculateProgress(challenge);

    return (
        <CollapsibleContent title={challenge.name}>
            <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
            <div className={"fw-bold"}>{challenge.start.toString()}   -----   {challenge.finish.toString()}</div>
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

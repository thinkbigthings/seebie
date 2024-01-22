import React, {useEffect, useState} from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Form from "react-bootstrap/Form";
import DatePicker from "react-datepicker";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationTriangle} from "@fortawesome/free-solid-svg-icons";

function ChallengeForm(props) {

    const {setEditableChallenge, editableChallenge, setDataValid, savedChallenges} = props;

    // validation of individual fields for validation feedback to the user
    const [dateOrderValid, setDateOrderValid] = useState(true);
    const [nameValid, setNameValid] = useState(true);
    const [nameUnique, setNameUnique] = useState(true);

    // this is a warning, so we don't disable the save button
    const [datesOverlap, setDatesOverlap] = useState(false);

    // Add a state variable to track user interaction with the name field
    const [nameTouched, setNameTouched] = useState(false);

    const validateChallenge = (challenge) => {

        // name validation to consider if the user has interacted with the field
        const nameValid = nameTouched
            ? (challenge.name !== '' && challenge.name.trim() === challenge.name)
            : true;
        const dateOrderValid = challenge.localStartTime < challenge.localEndTime;
        const allSavedChallengeDetails = savedChallenges.upcoming.concat(savedChallenges.completed).concat(savedChallenges.current);
        const nameUnique = !allSavedChallengeDetails.some(challengeDetails => challengeDetails.challenge.name === challenge.name);
        const datesOverlap = allSavedChallengeDetails.map(details => details.challenge).some(c => {
            return (challenge.localStartTime >= c.exactStart && challenge.localStartTime <= c.exactFinish)
                || (challenge.localEndTime >= c.exactStart && challenge.localEndTime <= c.exactFinish);
        });

        setDateOrderValid(dateOrderValid);
        setNameValid(nameValid);
        setNameUnique(nameUnique);
        setDatesOverlap(datesOverlap);
        setDataValid(dateOrderValid && nameValid && nameUnique);
    };

    // useEffect to run validation on component mount and whenever editableChallenge changes
    // This is so the validation is run when the form is populated from outside
    // (e.g. when the user selects a predefined challenge)
    // and not just when the user edits the form
    useEffect(() => {
        validateChallenge(editableChallenge);
    }, [editableChallenge]);

    const updateChallenge = (updateValues) => {
        // Set nameTouched to true if the name field is being updated
        if (updateValues.name !== undefined) {
            setNameTouched(true);
        }
        const updatedChallenge = {...editableChallenge, ...updateValues};
        setEditableChallenge(updatedChallenge);
        validateChallenge(updatedChallenge);
    }


    return (
       <>
           <Form>
               <Container className="ps-0">
                   <label htmlFor="challengeName" className="form-label">Short Name</label>
                   <Form.Control.Feedback type="invalid"
                                          className={"d-inline ms-1 " + ((!nameUnique) ? 'visible' : 'invisible')}>
                       This name is already used
                   </Form.Control.Feedback>
                   <Form.Control
                       type="text"
                       className="form-control"
                       id="challengeName"
                       placeholder=""
                       value={editableChallenge.name}
                       onChange={e => updateChallenge({name: e.target.value})}
                       isInvalid={!nameValid || !nameUnique}
                   />
               </Container>
               <Form.Control.Feedback type="invalid"
                                      className={"mh-24px d-block " + ((!nameValid) ? 'visible' : 'invisible')}>
                   Can't be empty or have space at the ends
               </Form.Control.Feedback>
               <Container className="ps-0 mb-3">
                   <label type="text" htmlFor="description" className="form-label">Description</label>
                   <textarea rows="6" className="form-control" id="description" placeholder=""
                             value={editableChallenge.description}
                             onChange={e => updateChallenge({description: e.target.value})}/>
               </Container>

               <Container className="ps-0">
                   <label htmlFor="startDate" className="form-label">Start Date</label>
                   <div>
                       <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                   id="startDate" dateFormat="MMMM d, yyyy"
                                   onChange={date => updateChallenge({localStartTime: date})}
                                   selected={editableChallenge.localStartTime}/>
                   </div>
                   <Form.Control.Feedback type="invalid"
                                          className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                       Start date must be before end date
                   </Form.Control.Feedback>
               </Container>
               <Container className="ps-0">
                   <label htmlFor="endDate" className="form-label">End Date</label>
                   <div>
                       <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                   id="startDate" dateFormat="MMMM d, yyyy"
                                   onChange={date => updateChallenge({localEndTime: date})}
                                   selected={editableChallenge.localEndTime}/>
                   </div>
                   <Form.Control.Feedback type="invalid"
                                          className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                       End date must be after start date
                   </Form.Control.Feedback>
               </Container>
           </Form>
           <label className={"text-warning " + ((datesOverlap) ? 'visible' : 'invisible')}>
               <FontAwesomeIcon icon={faExclamationTriangle} className={"pe-1"}/>
               This date range overlaps another challenge which is not recommended
           </label>
       </>
    );
}

export default ChallengeForm;

import React, {useState} from 'react';
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


    const updateChallenge = (updateValues) => {

        let updatedChallenge = {...editableChallenge, ...updateValues};

        let updatedDateOrderValid = updatedChallenge.localStartTime < updatedChallenge.localEndTime;
        setDateOrderValid(updatedDateOrderValid);

        let updatedNameValid = updatedChallenge.name !== '' && updatedChallenge.name.trim() === updatedChallenge.name
        setNameValid(updatedNameValid);

        let allSavedChallengeDetails = savedChallenges.upcoming.concat(savedChallenges.completed).concat(savedChallenges.current);
        let updatedNameUnique = ! allSavedChallengeDetails.some(challengeDetails => challengeDetails.challenge.name === updatedChallenge.name);
        setNameUnique(updatedNameUnique);

        // Query for challenges where the given start is between challenge start/finish and same for given finish
        let updatedDatesOverlap = allSavedChallengeDetails.map(details => details.challenge).some(c => {
            return (updatedChallenge.localStartTime >= c.exactStart && updatedChallenge.localStartTime <= c.exactFinish)
                || (updatedChallenge.localEndTime >= c.exactStart && updatedChallenge.localEndTime <= c.exactFinish);
        });
        setDatesOverlap(updatedDatesOverlap);

        setDataValid( updatedDateOrderValid && updatedNameValid && updatedNameUnique);
        setEditableChallenge(updatedChallenge);
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
                   Name cannot be empty or have space at the ends
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

import React, {useEffect, useState} from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Form from "react-bootstrap/Form";
import DatePicker from "react-datepicker";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationTriangle} from "@fortawesome/free-solid-svg-icons";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {ChallengeData, ChallengeList} from "./types/challenge.types";

function ChallengeForm(props:{
                            setEditableChallenge:React.Dispatch<React.SetStateAction<ChallengeData>>
                            editableChallenge:ChallengeData,
                            setDataValid:React.Dispatch<React.SetStateAction<boolean>>,
                            savedChallenges:ChallengeList<ChallengeData>}
                        ) {

    const {setEditableChallenge, editableChallenge, setDataValid, savedChallenges} = props;

    // validation of individual fields for validation feedback to the user
    const [dateOrderValid, setDateOrderValid] = useState(true);
    const [nameValid, setNameValid] = useState(true);
    const [nameUnique, setNameUnique] = useState(true);

    // this is a warning, so we don't disable the save button
    const [datesOverlap, setDatesOverlap] = useState(false);

    // Add a state variable to track user interaction with the name field
    const [nameTouched, setNameTouched] = useState(false);


    const validateChallenge = (challenge: ChallengeData) => {

        // name validation to consider if the user has interacted with the field
        const nameValid = nameTouched
            ? (challenge.name !== '' && challenge.name.trim() === challenge.name)
            : true;
        const dateOrderValid = challenge.localStartTime < challenge.localEndTime;
        const allSavedChallenges = savedChallenges.upcoming.concat(savedChallenges.completed).concat(savedChallenges.current);
        const nameUnique = !allSavedChallenges.some(saved => saved.name === challenge.name);


        // TODO make an overlap method for ChallengeFormData and ChallengeDetailDto
        // Is the exactStart and exactFinish updated if the form data is updated? Should it really be on the dto after coming from the server?

        const datesOverlap = allSavedChallenges.some(saved => {
            return (challenge.localStartTime >= saved.exactStart && challenge.localStartTime <= saved.exactFinish)
                || (challenge.localEndTime >= saved.exactStart && challenge.localEndTime <= saved.exactFinish);
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

    const updateChallenge = (updateValues: Partial<ChallengeData> ) => {
        const updatedChallenge:ChallengeData = {...editableChallenge, ...updateValues};
        setEditableChallenge(updatedChallenge);
        validateChallenge(updatedChallenge);
    }


    return (
       <>
           <Form>
               <Container className="ps-0 pe-0">
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
                       onChange={e => {
                           setNameTouched(true);
                           updateChallenge({name: e.target.value})}
                       }
                       isInvalid={!nameValid || !nameUnique}
                   />
               </Container>
               <Form.Control.Feedback type="invalid"
                                      className={"mh-24px d-block " + ((!nameValid) ? 'visible' : 'invisible')}>
                   Can't be empty or have space at the ends
               </Form.Control.Feedback>
               <Container className="ps-0 mb-3 pe-0">
                   <textarea rows={6} className="form-control" id="description" placeholder="Description"
                             value={editableChallenge.description}
                             onChange={e => updateChallenge({description: e.target.value})}/>
               </Container>

               <Container id="dateRangeId" className="p-0">
                   <Row className={"pb-2"}>
                       <Col md={6} className={"col-4 "}>
                           <label htmlFor="startDate" className="form-label">Start Date</label>
                       </Col>
                       <Col md={6} className={"col-8 "}>
                           <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                       id="startDate" dateFormat="MMMM d, yyyy"
                                       onChange={date => {if(date) updateChallenge({localStartTime: date})}}
                                       selected={editableChallenge.localStartTime}/>
                       </Col>
                   </Row>
                   <Row className={"pb-2"}>
                       <Col md={6} className={"col-4 "}>
                           <label htmlFor="endDate" className="form-label">End Date</label>
                       </Col>
                       <Col md={6} className={"col-8 "}>
                           <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                       id="startDate" dateFormat="MMMM d, yyyy"
                                       onChange={date => {if(date) updateChallenge({localEndTime: date})}}
                                       selected={editableChallenge.localEndTime}/>
                       </Col>
                   </Row>
                   <Row>
                       <Form.Control.Feedback type="invalid"
                                              className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                           End date must be after start date
                       </Form.Control.Feedback>
                   </Row>
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

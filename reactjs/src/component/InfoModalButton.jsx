import React, { useState } from 'react';
import { Button, Modal } from 'react-bootstrap';
import {faQuestion} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const InfoModalButton = ({ titleText, modalText }) => {
  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  return (
    <>
      <Button variant="secondary" onClick={handleShow}>
          <FontAwesomeIcon className="app-highlight ms-2" icon={faQuestion} />
      </Button>

      <Modal show={show} onHide={handleClose} onClick={handleClose} centered >
        <Modal.Header closeButton>
          <Modal.Title>{titleText}</Modal.Title>
        </Modal.Header>
        <Modal.Body>{modalText}</Modal.Body>
      </Modal>
    </>
  );
};

export default InfoModalButton;

// @ts-nocheck
import React, {useState} from 'react';
import {Button, Modal} from 'react-bootstrap';
import Alert from "react-bootstrap/Alert";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";

const WarningButton = (props) => {

    const {buttonText, onConfirm} = props;

    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    return (
        <>
            <Button variant="danger" onClick={handleShow}>
                <FontAwesomeIcon className="me-2" icon={faTrash}/>
                {buttonText}
            </Button>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Warning</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="warning">
                        {props.children}
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>Cancel</Button>
                    <Button variant="warning" onClick={() => {onConfirm(); handleClose();}}>{buttonText}</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default WarningButton;

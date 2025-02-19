import { useState } from "react";
import { Button, Modal } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faQuestion } from "@fortawesome/free-solid-svg-icons";

interface InfoModalButtonProps {
    titleText: string;
    modalText: string;
    className?: string;
}

const InfoModalButton = ({ titleText, modalText, className }: InfoModalButtonProps) => {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    return (
        <>
            <Button variant="secondary" className={`rounded-circle ${className || ""}`} onClick={handleShow}>
                <FontAwesomeIcon className="app-highlight" icon={faQuestion} />
            </Button>

            <Modal show={show} onHide={handleClose} onClick={handleClose} centered>
                <Modal.Header className="app-faq-button border-secondary border-bottom-2" closeButton>
                    <Modal.Title>{titleText}</Modal.Title>
                </Modal.Header>
                <Modal.Body className="app-highlight app-faq-button">{modalText}</Modal.Body>
            </Modal>
        </>
    );
};

export default InfoModalButton;

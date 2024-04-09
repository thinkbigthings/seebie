// @ts-nocheck
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import Alert from "react-bootstrap/Alert";


function SuccessModal(props) {

    const {title, showing, handleClose} = props;

    return (
        <Modal centered={true} show={showing} onHide={handleClose} >
            <Modal.Header closeButton>
                <Modal.Title>{title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Alert variant="success">
                    {props.children}
                </Alert>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={handleClose} >OK</Button>
            </Modal.Footer>
        </Modal>
    );
}

export default SuccessModal;

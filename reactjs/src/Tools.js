import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import useCurrentUser from "./useCurrentUser";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";
import Form from 'react-bootstrap/Form';
import Row from "react-bootstrap/Row";
import {FormLabel} from "react-bootstrap";

function Tools() {

    const {currentUser} = useCurrentUser();
    const username = currentUser.username;

    const [selectedFile, setSelectedFile] = useState();
    const [isFilePicked, setIsFilePicked] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [uploadSuccessInfo, setUploadSuccessInfo] = useState({numImported: 0, username: username});

    const downloadUrl = "/user/" + username + "/sleep/download";
    const uploadUrl = "/user/" + username + "/sleep/upload";

    console.log(selectedFile);

    const changeHandler = (event) => {
        setSelectedFile(event.target.files[0]);
        setIsFilePicked(true);
    }

    const onUploadSuccess = (uploadResponse) => {
        setUploadSuccessInfo(uploadResponse);
        setShowSuccessModal(true);
        setIsFilePicked(false);
    }

    function handleSubmission() {

        const formData = new FormData();
        formData.append('file', selectedFile);

        // Do not set content type, the browser will correctly set that
        // (and in fact this will break if you try to add "Content-type": "multipart/form-data")
        const requestMeta = {
            method: 'POST',
            body: formData
        };

        return fetch(uploadUrl, requestMeta)
            .then((response) => response.json())
            .then(onUploadSuccess)
            .catch((error) => console.error('Error:', error));
    }

    return (
        <Container>

            <Modal centered={true} show={showSuccessModal} onHide={() => setShowSuccessModal(false)} >
                <Modal.Header closeButton>
                    <Modal.Title>Upload Success</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="success">
                        Uploaded {uploadSuccessInfo.numImported} records for user {username}.
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={()=> setShowSuccessModal(false)} >OK</Button>
                </Modal.Footer>
            </Modal>

            <NavHeader title="Tools" />

            <hr />

            <Container className="mx-0 my-5">
                <h4 className="mb-3" >Export</h4>
                <label className="d-block mb-3">You have sleep records that you can download to CSV</label>
                <a href={downloadUrl}>
                    <Button variant="secondary" >
                        <FontAwesomeIcon className="me-2" icon={faDownload} />
                        Download
                    </Button>
                </a>
            </Container>

            <hr />

            <Container className="mx-0 my-5">
                <h4 className="mb-3" >Import</h4>

                <Form.Group controlId="formFile" className="mb-3">
                    <Form.Label>Select CSV file with sleep data to upload</Form.Label>
                    <Form.Control type="file" name={"file"} onChange={changeHandler} />

                    {
                        isFilePicked ?
                            <Container>
                                <Row>File name: {selectedFile.name} </Row>
                                <Row>File type: {selectedFile.type} </Row>
                                <Row>File size: {selectedFile.size} bytes </Row>
                                <Row>lastModifiedDate:{' '}  {selectedFile.lastModifiedDate.toLocaleDateString()}</Row>

                            </Container>
                            : ""
                    }

                </Form.Group>

                <Button variant="secondary" onClick={handleSubmission} disabled={ ! isFilePicked} >
                    <FontAwesomeIcon className="me-2" icon={faUpload} />
                    Upload
                </Button>

            </Container>

        </Container>
    );
}


export default Tools;

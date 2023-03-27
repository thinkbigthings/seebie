import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import useCurrentUser from "./useCurrentUser";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";

function Tools() {

    const {currentUser} = useCurrentUser();
    const username = currentUser.username;

    const [selectedFile, setSelectedFile] = useState();
    const [isFilePicked, setIsFilePicked] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [uploadSuccessInfo, setUploadSuccessInfo] = useState({numImported: 0, username: username});

    const downloadUrl = "/user/" + username + "/sleep/download";
    const uploadUrl = "/user/" + username + "/sleep/upload";

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

            <p>
                <a href={downloadUrl}>
                    <Button variant="secondary" >
                        <FontAwesomeIcon className="me-2" icon={faDownload} />
                        Download sleep data to CSV file
                    </Button>
                </a>
            </p>

            {isFilePicked ? (

                <div>
                    <p>Filename: {selectedFile.name}</p>
                    <p>Filetype: {selectedFile.type}</p>
                    <p>Size in bytes: {selectedFile.size}</p>
                    <p>
                        lastModifiedDate:{' '}
                        {selectedFile.lastModifiedDate.toLocaleDateString()}
                    </p>
                </div>

            ) : (
                <p>Select a file to show details</p>
            )}
            <p>
                <input type="file" name="file" onChange={changeHandler} />
                <Button variant="secondary" onClick={handleSubmission} disabled={ ! isFilePicked} >
                    <FontAwesomeIcon className="me-2" icon={faUpload} />
                    Upload CSV file with sleep data
                </Button>
            </p>

        </Container>
    );
}


export default Tools;

import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import Form from 'react-bootstrap/Form';
import Row from "react-bootstrap/Row";
import useHttpError from "./hooks/useHttpError";
import {useApiGet} from "./hooks/useApiGet";
import {useParams} from "react-router-dom";
import SuccessModal from "./component/SuccessModal";

function Tools() {

    const {username} = useParams();

    const {throwOnHttpError} = useHttpError();

    const [selectedFile, setSelectedFile] = useState({});
    const [isFilePicked, setIsFilePicked] = useState(false);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [uploadSuccessInfo, setUploadSuccessInfo] = useState({numImported: 0, username: username});

    const downloadUrl = '/api/user/' + username + '/sleep/download';
    const uploadUrl = '/api/user/' + username + '/sleep/upload';

    const sleepUrl = '/api/user/' + username + '/sleep';

    // so we can retrieve the total number of records for the user
    const [data] = useApiGet(sleepUrl, 1, 0);
    const numUserRecords = data.totalElements;

    const onFilePicked = (event) => {
        setSelectedFile(event.target.files[0]);
        setIsFilePicked(true);
    }


    const isCsv = isFilePicked && selectedFile.type === "text/csv";


    const onUploadSuccess = (uploadResponse) => {
        setUploadSuccessInfo(uploadResponse);
        setShowSuccessModal(true);
        setSelectedFile({});
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
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(onUploadSuccess)
            .catch((error) => console.error('Error:', error));
    }

    return (
        <Container>

            <SuccessModal title="Upload Success" showing={showSuccessModal} handleClose={() => setShowSuccessModal(false)}>
                Uploaded {uploadSuccessInfo.numImported} records for user {username}.
            </SuccessModal>

            <NavHeader title="Tools" />

            <hr />

            <Container className="mx-0 px-0 my-5">
                 <h4 className="mb-3" >Export</h4>
                <label className="d-block mb-3">You have {numUserRecords} sleep records that you can download to CSV</label>
                <a href={downloadUrl}>
                    <Button variant="secondary" >
                        <FontAwesomeIcon className="app-highlight me-2" icon={faDownload} />
                        Download
                    </Button>
                </a>
            </Container>

            <hr />

            <Container className="mx-0 px-0 my-5">
                <h4 className="mb-3" >Import</h4>

                <Form.Group controlId="formFile" className="mb-3">
                    <Form.Label>Select CSV file with sleep data to upload</Form.Label>
                    <Form.Control type="file" name={"file"} onChange={onFilePicked} />
                    {
                        (isFilePicked) && (! isCsv) ?
                            <Container>
                                <Row>You must select a CSV file</Row>
                            </Container>
                        : ""
                    }
                    {
                        (isFilePicked && isCsv) ?
                            <Container>
                                <Row>File: {selectedFile.name} </Row>
                                <Row>Last Modified: {selectedFile.lastModifiedDate.toLocaleDateString()}</Row>
                                <Row>Size: {selectedFile.size} bytes </Row>
                            </Container>
                        : ""
                    }
                </Form.Group>

                <Button variant="secondary" onClick={handleSubmission} disabled={ (!isFilePicked) || (!isCsv) } >
                    <FontAwesomeIcon className="app-highlight me-2" icon={faUpload} />
                    Upload
                </Button>

            </Container>

        </Container>
    );
}


export default Tools;

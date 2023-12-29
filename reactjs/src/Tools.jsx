import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import Form from 'react-bootstrap/Form';
import useHttpError from "./hooks/useHttpError";
import {useApiGet} from "./hooks/useApiGet";
import {useParams} from "react-router-dom";
import SuccessModal from "./component/SuccessModal";

function Tools() {

    const { username } = useParams();
    const { throwOnHttpError } = useHttpError();

    const [selectedFile, setSelectedFile] = useState(null);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [uploadSuccessInfo, setUploadSuccessInfo] = useState({ numImported: 0, username: username });

    const downloadUrl = `/api/user/${username}/sleep/download`;
    const uploadUrl = `/api/user/${username}/sleep/upload`;
    const sleepUrl = `/api/user/${username}/sleep`;

    const [data] = useApiGet(sleepUrl, 1, 0);
    const numSleepRecords = data.totalElements;

    const onFilePicked = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const onUploadSuccess = (uploadResponse) => {
        setUploadSuccessInfo(uploadResponse);
        setShowSuccessModal(true);
        setSelectedFile(null);
    };

    const handleSubmission = () => {
        const formData = new FormData();
        formData.append('file', selectedFile);

        // Do not set content type, the browser will correctly set that
        // (and in fact this will break if you try to add "Content-type": "multipart/form-data")
        const requestMeta = {
            method: 'POST',
            body: formData
        };

        fetch(uploadUrl, requestMeta)
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(onUploadSuccess)
            .catch((error) => console.error('Error:', error));
    };

    const isCsv = selectedFile && selectedFile.type === "text/csv";

    const showValidMessage = isCsv && selectedFile;
    const showInvalidMessage = !isCsv && selectedFile;
    const validStyle = showValidMessage ? "was-validated" : "";

    console.log(validStyle);

    return (
        <Container>
            <SuccessModal title="Upload Success" showing={showSuccessModal} handleClose={() => setShowSuccessModal(false)}>
                Uploaded {uploadSuccessInfo.numImported} records for user {username}.
            </SuccessModal>

            <NavHeader title="Tools" />

            <hr />

            <Container className="mx-0 px-0 my-5">
                <h4 className="mb-3">Export</h4>
                <label className="d-block mb-3">You have {numSleepRecords} sleep records that you can download to CSV</label>
                <a href={downloadUrl}>
                    <Button variant="secondary">
                        <FontAwesomeIcon className="app-highlight me-2" icon={faDownload} />
                        Download
                    </Button>
                </a>
            </Container>

            <hr />

            <Container className="mx-0 px-0 my-5">
                <h4 className="mb-3">Import</h4>

                <Form>
                    <Form.Group controlId="formFile" className={validStyle + " mb-3"}>
                        <Form.Label>Select CSV file with sleep data to upload</Form.Label>
                        <Form.Control type="file" name="file" onChange={onFilePicked} isInvalid={showInvalidMessage} />
                        <Form.Control.Feedback type="invalid">
                            Please select a CSV file.
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Button variant="secondary" onClick={handleSubmission} disabled={!isCsv}>
                        <FontAwesomeIcon className="app-highlight me-2" icon={faUpload} />
                        Upload
                    </Button>
                </Form>
            </Container>
        </Container>
    );
}

export default Tools;
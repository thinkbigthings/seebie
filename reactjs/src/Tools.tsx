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
import {SleepDetailDto} from "./types/sleep.types";

interface UploadResponse {
    numImported: number,
    username: string
}

const initialSelectedFile:File|null = null;

function Tools() {

    const { username } = useParams();
    const { throwOnHttpError } = useHttpError();

    const [fileKey, setFileKey] = useState(Date.now());
    const [fileIsBeingSelected, setFileIsBeingSelected] = useState(false);
    const [csvSelected, setCsvSelected] = React.useState(true);
    const [jsonSelected, setJsonSelected] = React.useState(false);
    const [selectedFile, setSelectedFile] = useState(initialSelectedFile);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [uploadSuccessInfo, setUploadSuccessInfo] = useState({ numImported: 0, username: username });

    const downloadCsv = `/api/user/${username}/export/csv`;
    const uploadCsv = `/api/user/${username}/import/csv`;
    const downloadJson = `/api/user/${username}/export/json`;
    const uploadJson = `/api/user/${username}/import/json`;
    const sleepUrl = `/api/user/${username}/sleep`;

    const downloadUrl = csvSelected ? downloadCsv : downloadJson;

    const {data, pagingControls} = useApiGet<SleepDetailDto>(sleepUrl, 1, 0);
    const numSleepRecords = data.totalElements;

    const onFilePicked = (event:React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files.length > 0) {
            setSelectedFile(event.target.files[0]);
            setFileIsBeingSelected(true);
        } else {
            setSelectedFile(null);
            setFileIsBeingSelected(false);
        }
    };

    const isUploadCsv = selectedFile ? selectedFile.type === "text/csv" : false;
    const isUploadJson = selectedFile ? selectedFile.type === "application/json" : false;
    const uploadInvalid = fileIsBeingSelected && !(isUploadCsv || isUploadJson);

    const swapDownloadTypeSelection = () => {
        setCsvSelected(!csvSelected);
        setJsonSelected(!jsonSelected);
    }

    const onUploadSuccess = (uploadResponse: UploadResponse) => {
        setFileKey(Date.now());  // Changing the key will remount the input and reset its value
        setUploadSuccessInfo(uploadResponse);
        setShowSuccessModal(true);
        setSelectedFile(null);
        setFileIsBeingSelected(false);
    };

    const handleSubmission = () => {

        if(selectedFile === null) {
            console.log("No file selected, so can't handle submission");
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);

        // Do not set content type, the browser will correctly set that
        // (and in fact this will break if you try to add "Content-type": "multipart/form-data")
        const requestMeta = {
            method: 'POST',
            body: formData
        };

        const uploadUrl = isUploadCsv ? uploadCsv : uploadJson;

        fetch(uploadUrl, requestMeta)
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(onUploadSuccess)
            .catch((error) => console.error('Error:', error));
    };

    return (
        <Container>
            <SuccessModal title="Upload Success" showing={showSuccessModal} handleClose={() => setShowSuccessModal(false)}>
                Uploaded {uploadSuccessInfo.numImported} records for user {username}.
            </SuccessModal>

            <NavHeader title="Tools" />

            <Container className="mx-0 px-0 py-2 border-top border-light-subtle">
                <h4 className="mb-3">Export</h4>
                <label className="d-block mb-3">You have {numSleepRecords} sleep records that you can download</label>
                <div className={"mb-3"}>
                    <Form.Check
                        name="download-format"
                        type="radio"
                        id="radio-csv"
                        label="CSV (sleep data only)"
                        checked={csvSelected}
                        onChange={swapDownloadTypeSelection}
                    />
                    <Form.Check
                        name="download-format"
                        type="radio"
                        id="radio-json"
                        label="JSON (sleep and challenges)"
                        checked={jsonSelected}
                        onChange={swapDownloadTypeSelection}
                    />
                </div>
                <a href={downloadUrl}>
                    <Button className="mb-3" variant="secondary">
                        <FontAwesomeIcon className="app-highlight me-2" icon={faDownload} />
                        Download
                    </Button>
                </a>
            </Container>

            <Container className="mx-0 px-0 py-2 border-top border-light-subtle">
                <h4 className="mb-3">Import</h4>

                <Form>
                    <Form.Group controlId="formFile">
                        <Form.Label>Select file with sleep data to upload</Form.Label>
                        <Form.Control
                            key={fileKey}
                            type="file"
                            name="file"
                            onChange={onFilePicked}
                            isValid={isUploadCsv}
                            isInvalid={uploadInvalid}
                        />
                        <Form.Control.Feedback type="invalid"
                                               className={"d-block " + ((uploadInvalid) ? 'visible' : 'invisible')}>
                            Please select a csv or json file.
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Button variant="secondary" onClick={handleSubmission} disabled={uploadInvalid}>
                        <FontAwesomeIcon className="app-highlight me-2" icon={faUpload}/>
                        Upload
                    </Button>
                </Form>
            </Container>
        </Container>
    );
}

export default Tools;
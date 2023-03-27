import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import useCurrentUser from "./useCurrentUser";
import useApiPost from "./useApiPost";

function Tools() {

    const {currentUser} = useCurrentUser();
    const post = useApiPost();

    const [selectedFile, setSelectedFile] = useState();
    const [isFilePicked, setIsFilePicked] = useState(false);

    const downloadUrl = "/user/" + currentUser.username + "/sleep/download";
    const uploadUrl = "/user/" + currentUser.username + "/sleep/upload";

    const changeHandler = (event) => {
        setSelectedFile(event.target.files[0]);
        setIsFilePicked(true);
    }

    function handleSubmission() {

        const formData = new FormData();
        formData.append('file', selectedFile);

        // post(uploadUrl, formData)
        //     .then((response) => response.json())
        //     .then((result) => {
        //         console.log('Success:', result);
        //     })
        //     .catch((error) => {
        //         console.error('Error:', error);
        //     });
    }

    return (
        <Container>

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

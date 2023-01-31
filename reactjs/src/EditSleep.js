import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";

const blankSleep = {
    dateAwakened: "2000-01-01",
    minutes: 480,
    notes: '',
    outOfBed: 0,
    tags: [],
}

function EditSleep({history, match}) {

    const username = match.params.username;
    const sleepId = match.params.sleepId;

    const sleepEndpoint = '/user/' + username + '/sleep/' + sleepId;

    const [data, setData] = useState(blankSleep);

    useEffect(() => {
        fetch(sleepEndpoint)
            .then(response => response.json())
            .then(setData)
    }, [setData]);

    // update data

    // const put = useApiPut();
    // const onSave = (personalInfo) => {
    //     put(userInfoEndpoint, personalInfo).then(history.goBack);
    // }

    return (
        <div className="container mt-3">

            <h1>Sleep Session</h1>

            <Container id="sleepFormWrapper" className="pl-0 pr-0">
                Coming soon...
                {/*<EditSleep />*/}
                {/*<UserForm onCancel={history.goBack} onSave={onSave} userData={data}/>*/}
            </Container>
        </div>
    );
}

export default EditSleep;

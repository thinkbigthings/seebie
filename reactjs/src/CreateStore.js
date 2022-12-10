import React, {useState} from 'react';

import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

import useApiPost from "./useApiPost";

const blankFormData = {
    name: '',
    website: '',
}

function CreateStore() {

    const post = useApiPost();
    const [showCreateStore, setShowCreateStore] = useState(false);
    const [store, setStore] = useState(blankFormData);

    const onCreate = (storeData) => {

        const registrationRequest = {
            name: storeData.name,
            website: storeData.website,
            updated: null
        }

        const requestBody = typeof registrationRequest === 'string' ? registrationRequest : JSON.stringify(registrationRequest);

        post('/store', requestBody)
            .then(result => setShowCreateStore(false));
    }

    function updateStore(updateValues) {
        setStore( {...store, ...updateValues});
    }

    function onHide() {
        setStore(blankFormData);
        setShowCreateStore(false);
    }

    function onConfirm() {
        setStore(blankFormData);
        onCreate({...store});
    }

    return (
        <>
            <Button variant="success" onClick={() => setShowCreateStore(true)}>Register Store</Button>

            <Modal show={showCreateStore} onHide={onHide} >
                <Modal.Header closeButton>
                    <Modal.Title>Register Store</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="mb-3">
                        <label htmlFor="name" className="form-label">Name</label>
                        <input type="text" className="form-control" id="name" placeholder="Name"
                               value={store.name}
                               onChange={e => updateStore({name : e.target.value })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="website" className="form-label">Website</label>
                        <input type="url" className="form-control" id="website" placeholder="Website"
                               value={store.website}
                               onChange={e => updateStore({website : e.target.value })} />
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>Close</Button>
                    <Button variant="primary" onClick={onConfirm} >Save</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default CreateStore;

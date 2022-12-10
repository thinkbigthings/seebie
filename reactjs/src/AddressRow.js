import React from 'react';

import Button from "react-bootstrap/Button";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEdit, faTrash} from "@fortawesome/free-solid-svg-icons";

const addressToString = (address) => {
    return address.line1 + ', ' + address.city + ' ' + address.state + ' ' + address.zip;
}

function AddressRow(props) {

    const {currentAddress, onEdit, onDelete} = props;

    return (

        <Row key={addressToString(currentAddress)} className="pt-2 pb-2 border-bottom border-top ">
            <Col xs="9">
                {addressToString(currentAddress)}
            </Col>
            <Col xs="3">
                <Button variant="primary" className="mr-2" onClick={onEdit}>
                    <FontAwesomeIcon className="me-2" icon={faEdit} />
                    Edit
                </Button>
                <Button variant="danger"  onClick={onDelete}>
                    <FontAwesomeIcon className="me-2" icon={faTrash} />
                    Delete
                </Button>
            </Col>
        </Row>
    );
}

export default AddressRow;

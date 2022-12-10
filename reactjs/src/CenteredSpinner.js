import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import React from "react";

function CenteredSpinner() {
    return (
        <Container>
            <Row className="text-center">
                <Col xs="12" className="pt-5">
                    <div className="d-flex justify-content-center">
                        <div className="spinner-border text-secondary" role="status">
                            <span className="sr-only">Loading...</span>
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}


export default CenteredSpinner;
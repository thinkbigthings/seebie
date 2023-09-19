import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";


function DateRangePicker(props) {

    const {selectedStart, onChangeStart, selectedEnd, onChangeEnd} = props;

    return (
        <Container>
            <Row className="pb-3">
                <Col className="col-2">
                    <label className="d-inline-block" htmlFor="startDate">From</label>
                </Col>
                <Col className="col-md-4">
                    <DatePicker
                        className="form-control d-inline-block" id="startDate" dateFormat="MMMM d, yyyy"
                        onChange={onChangeStart}
                        selected={selectedStart}
                    />
                </Col>
            </Row>
            <Row className={"pb-3"}>
                <Col className="col-2">
                    <label htmlFor="endDate">To</label>
                </Col>
                <Col className="col-md-4">
                    <DatePicker
                        className="form-control" id="endDate" dateFormat="MMMM d, yyyy"
                        onChange={onChangeEnd}
                        selected={selectedEnd}
                    />
                </Col>
            </Row>
        </Container>
    );

}

export default DateRangePicker;

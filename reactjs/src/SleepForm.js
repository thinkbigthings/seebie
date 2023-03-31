import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";


function SleepForm(props) {

    const {onChange, data} = props;

    const CustomInput = ({ value, onClick }) => {
        return (
            <Button onClick={onClick}>
                {value}
            </Button>
        );
    }

    const MyDatePicker = ({selectTime, onSelection}) => {
        return (
            <DatePicker
                selected={selectTime}
                className="form-control" id="dateStart" placeholder="Start Time"
                dateFormat="MMMM d, yyyy h:mm aa"
                showTimeSelect
                timeIntervals={15}
                timeCaption="time"
                timeFormat="p"
                onChange={ onSelection }
                customInput={<CustomInput />}
            />
        );
    }

    return (
        <Container id="userFormId" className="ps-0 ">
            <Row>
                <Col md={6} className={"pe-4"}>
                    <div className="mb-3">
                        <label htmlFor="dateStart" className="form-label">Time Fell Asleep</label>
                        <MyDatePicker selectTime={data.startTime} onSelection={ date => onChange({startTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="dateEnd" className="form-label">Time Woke Up</label>
                        <MyDatePicker selectTime={data.stopTime} onSelection={ date => onChange({stopTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="minutesAwake" className="form-label">Minutes Awake During Sleep Period</label>
                        <input type="text" className="form-control w-25" id="minutesAwake"
                               placeholder="Minutes Awake During Sleep Period"
                               value={data.minutesAwake}
                               onChange={e => onChange({minutesAwake : e.target.value})} />
                    </div>
                </Col>
                <Col md={6} className={"pe-0"}>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea rows="8" className="form-control" id="notes" placeholder="Notes"
                                  value={data.notes}
                                  onChange={e => onChange({notes : e.target.value })} />
                    </div>
                </Col>
            </Row>

        </Container>

    );
}

export {SleepForm};

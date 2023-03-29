import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import SleepDataManager from "./SleepDataManager";


function SleepForm(props) {

    const {onChange, data} = props;

    return (
        <Container id="userFormId" className="ps-0 " >

            <form>

                <div className="mb-3">
                        <label htmlFor="dateStart" className="form-label">Sleep Session Start</label>
                        <DatePicker
                            className="form-control" id="dateStart" placeholder="Start Time"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={data.startTime}
                            onChange={ date => onChange({startTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="dateEnd" className="form-label">Sleep Session End</label>
                        <DatePicker
                            className="form-control" id="dateEnd" placeholder="Stop Time"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={data.stopTime}
                            onChange={ date => onChange({stopTime : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="calculatedMinutes" className="form-label">Time Asleep</label>
                        <input disabled className="form-control" id="calculatedMinutes" placeholder="Time Asleep"
                               value={SleepDataManager.formatDuration(data.startTime, data.stopTime)} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="minutesAwake" className="form-label">Time Awake During Sleep Period (minutes)</label>
                        <input type="text"  className="form-control" id="minutesAwake" placeholder="Time Awake During Sleep Period (minutes)"
                               value={data.minutesAwake}
                               onChange={e => onChange({minutesAwake : e.target.value})} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea className="form-control" id="notes" placeholder="Notes"
                               value={data.notes}
                               onChange={e => onChange({notes : e.target.value })} />
                    </div>

            </form>
        </Container>
    );
}

export {SleepForm};

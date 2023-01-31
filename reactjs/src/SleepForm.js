import React, {useState} from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";


const numericRegex=/^[0-9]+$/;

const minutesBetween = (date1, date2) => {
    let diff = (date2.getTime() - date1.getTime()) / 1000;
    diff /= 60;
    return Math.abs(Math.round(diff));
}

const minuteToHrMin = (minutes) => {
    const hr = Math.floor(minutes / 60);
    const m = minutes % 60;
    return hr + 'hr ' + m + 'm';
}

function SleepForm(props) {


    const {onCancel, onSave, sleepData} = props;

    let today = new Date();
    today.setHours(5, 45, 0);

    let yesterday = new Date(today.getTime());
    yesterday.setDate(today.getDate() - 1);
    yesterday.setHours(21, 45, 0);

    const displayTimeInit = minuteToHrMin(minutesBetween(yesterday, today));

    // TODO use incoming sleep data to construct the form values
    // such as subtract minutes from end date to set the start date
    // Can't get back to a time from minutes and LocalDate

    // TODO add feature for timezones

    // const initialFormData = {
    //     startDate: ,
    //     endDate: sleepData.dateAwakened,
    //     displayTime: displayTimeInit,
    //     notes: '',
    //     outOfBed: 0
    // }

    const [formData, setFormData] = useState(initialFormData);

    const formToDto = (formData) => {
        return {
            dateAwakened: formData.endDate,
            minutes: minutesBetween(formData.endDate, formData.startDate),
            notes: formData.notes,
            outOfBed: formData.outOfBed,
            tags: []
        }
    }

    // const dtoToForm = (sleepData) => {
    //     return {
    //         endDate: sleepData.dateAwakened,
    //         startDate: TODO
    //         notes: sleepData.notes,
    //         outOfBed: sleepData.outOfBed,
    //         tags: sleepData.tags
    //     }
    // }

    function updateForm(updateValues) {

        let updatedFormData = {...formData, ...updateValues};

        if( numericRegex.test(updatedFormData.outOfBed)) {
            updatedFormData.displayTime = minuteToHrMin(minutesBetween(updatedFormData.startDate, updatedFormData.endDate));
            setFormData( updatedFormData );
        }
    }

    return (
        <Container id="userFormId" className="mt-5 ps-0 " >

            <form>

                <div className="mb-3">
                        <label htmlFor="dateStart" className="form-label">Sleep Session Start</label>
                        <DatePicker
                            className="form-control" id="dateStart" placeholder="Date Start"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={formData.startDate}
                            onChange={ date => updateForm({startDate : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="dateEnd" className="form-label">Sleep Session End</label>
                        <DatePicker
                            className="form-control" id="dateEnd" placeholder="Date End"
                            dateFormat="MMMM d, yyyy h:mm aa"
                            showTimeSelect
                            timeFormat="HH:mm"
                            timeIntervals={15}
                            timeCaption="time"
                            timeFormat="p"
                            selected={formData.endDate}
                            onChange={ date => updateForm({endDate : date })} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="calculatedMinutes" className="form-label">Time Asleep</label>
                        <input disabled className="form-control" id="calculatedMinutes" placeholder="Time Asleep"
                               value={formData.displayTime} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="outOfBed" className="form-label">Out Of Bed (number of times)</label>
                        <input type="text"  className="form-control" id="outOfBed" placeholder="Out of Bed (number of times)"
                               value={formData.outOfBed}
                               onChange={e => updateForm({outOfBed : e.target.value})} />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="notes" className="form-label">Notes</label>
                        <textarea className="form-control" id="notes" placeholder="Notes"
                               value={formData.notes}
                               onChange={e => updateForm({notes : e.target.value })} />
                    </div>


                <Button variant="success" onClick={() => { onSave(formToDto(formData)) }}>Save</Button>
                <Button variant="light" onClick={onCancel}>Cancel</Button>

                </form>
        </Container>
    );
}

export {SleepForm, minuteToHrMin};

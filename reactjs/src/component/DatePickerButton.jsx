import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Button from "react-bootstrap/Button";


const ButtonWrapper = React.forwardRef(({value, onClick, className}, ref) => {
    return (
        <Button ref={ref} onClick={onClick} className={className}>
            {value}
        </Button>
    );
});

// This is a DatePicker that uses a button instead of an input field so that the keyboard does not pop up on mobile.
function DatePickerButton(props) {

    // TODO also apply className, id, placeholder, and set from caller appropriately
    const {selected, onChange} = props;

    return (
        <DatePicker
            selected={selected}
            className="form-control" id="dateStart" placeholder="Start Time"
            dateFormat="MMMM d, yyyy h:mm aa"
            showTimeSelect
            timeIntervals={15}
            timeCaption="time"
            timeFormat="p"
            onChange={ onChange }
            customInput={<ButtonWrapper />}
        />
    );

}

export default DatePickerButton;

// @ts-nocheck
import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Button from "react-bootstrap/Button";


const ButtonWrapper = React.forwardRef(({value, onClick, className}, ref) => {
    return (
        <Button ref={ref} onClick={onClick} className={"form-control"}>
            {value}
        </Button>
    );
});

// This is a DatePicker that uses a button instead of an input field so that the keyboard does not pop up on mobile.
function DatePickerButton(props) {

    // TODO also apply className, id, placeholder, and set from caller appropriately
    const {selected, onChange} = props;

    // see https://github.com/Hacker0x01/react-datepicker/issues/2099 about the wrapperClassName

    return (
        <DatePicker
            wrapperClassName="w-100 form-control"
            selected={selected}
            id="dateStart"
            placeholder="Start Time"
            dateFormat="MMM d, yyyy h:mm aa"
            showTimeSelect
            timeIntervals={15}
            timeCaption="time"
            timeFormat="p"
            onChange={ onChange }
            customInput={<ButtonWrapper  />}
        />
    );

}

export default DatePickerButton;

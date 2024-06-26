import React from 'react';
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css';
import Button from "react-bootstrap/Button";

// Define props for ButtonWrapper, extending button attributes for better control
interface ButtonWrapperProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    value?: string;
}

const ButtonWrapper = React.forwardRef<HTMLButtonElement, ButtonWrapperProps>(
    ({ className, value, ...rest }, ref) => {
        return (
            <Button ref={ref} {...rest} className={`form-control ${className || ''}`} >
                {value}
            </Button>
        );
    }
);


// This is a DatePicker that uses a button instead of an input field so that the keyboard does not pop up on mobile.
function DatePickerButton(props:{selected:Date, onChange: (date:Date) => void}) {

    // TODO also apply className, id, placeholder, and set from caller appropriately
    const {selected, onChange} = props;

    // see https://github.com/Hacker0x01/react-datepicker/issues/2099 about the wrapperClassName

    return (
        <DatePicker
            wrapperClassName="w-100 form-control"
            selected={selected}
            id="dateStart"
            placeholderText="Start Time"
            dateFormat="MMM d, yyyy h:mm aa"
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

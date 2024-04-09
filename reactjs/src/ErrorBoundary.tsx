// @ts-nocheck
import React from 'react';

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationTriangle} from "@fortawesome/free-solid-svg-icons";
import Container from "react-bootstrap/Container";

class ErrorBoundary extends React.Component {
    state = {
        hasError: false,
        error: { message: '', stack: '' },
        info: { componentStack: '' }
    };

    static getDerivedStateFromError = error => {
        return { hasError: true };
    };

    componentDidCatch = (error, info) => {
        this.setState({ error, info });
    };

    render() {
        const { hasError, error, info } = this.state;
        const { children } = this.props;
        if(hasError) {
            console.log("error is " + JSON.stringify(error));
            console.log("info is " + JSON.stringify(info));
        }
        return hasError
            ? <Container>
                <h1>
                    <FontAwesomeIcon icon={faExclamationTriangle} />
                    Something went wrong
                </h1>
            </Container>

            : children;
    }
}

export default ErrorBoundary;
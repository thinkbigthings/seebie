
import React from 'react';
import { Container } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';

interface ErrorBoundaryState {
    hasError: boolean;
    error: Error | null;
    info: React.ErrorInfo | null;
}

interface ErrorBoundaryProps {
    children: React.ReactNode;
}

class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
    constructor(props: ErrorBoundaryProps) {
        super(props);
        this.state = {
            hasError: false,
            error: null,
            info: null
        };
    }

    static getDerivedStateFromError(error: Error): ErrorBoundaryState {
        // Update state so the next render will show the fallback UI.
        return { hasError: true, error, info: null };
    }

    componentDidCatch(error: Error, info: React.ErrorInfo): void {
        // You can also log the error to an error reporting service
        console.error("ErrorBoundary caught an error", error, info);
        this.setState({ error, info });
    }

    render() {
        const { hasError, error, info } = this.state;
        const { children } = this.props;

        if (hasError) {
            // Optionally log error and info to the console or another logging service.
            console.error("Error:", error);
            console.error("Info:", info);

            return (
                <Container>
                    <h1>
                        <FontAwesomeIcon icon={faExclamationTriangle} />
                        Something went wrong.
                    </h1>
                    <details style={{ whiteSpace: 'pre-wrap' }}>
                        {error && error.toString()}
                        <br />
                        {info && info.componentStack}
                    </details>
                </Container>
            );
        }

        return children;
    }
}

export default ErrorBoundary;
import {Button, Card, Col, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Container from "react-bootstrap/Container";

function NotFound(props) {

    const navigate = useNavigate();

    const goHome = (e) => {
        e.preventDefault();
        if (props.user)
            navigate("/");
    }

    return (
        <Container className="d-flex mt-5 justify-content-center">
            <Col md={5}>
                <Card>
                    <Card.Header className="text-start pe-3">Error</Card.Header>
                    <Card.Body className="text-center pt-5">
                        <Card.Title className="mb-2">Not found</Card.Title>
                        <Card.Text>
                            Requested path was not found
                        </Card.Text>
                        <Button className="mt-5 mb-3" variant="primary" onClick={goHome}>
                            <FontAwesomeIcon icon="fa-solid fa-house" /> Home
                        </Button>
                    </Card.Body>
                </Card>
            </Col>
        </Container>
    );
}

export default NotFound
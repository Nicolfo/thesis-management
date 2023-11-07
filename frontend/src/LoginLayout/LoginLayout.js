import { useState } from 'react';
import { Form, Button, Alert, Col, Row, Image } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import thesisicon from './thesisicon.jpg';

function LoginForm(props) {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const [show, setShow] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    const handleLogin = async (credentials) => {
        try {
            //const user = await API.logIn(credentials);
            //props.setUser(user);
        } catch (err) {
            throw err;
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        const credentials = { username, password };

        handleLogin(credentials)
            .then(() => navigate("/"))
            .catch((err) => {
                setErrorMessage(err.error);
                setShow(true);
            });
    };

    return (
        <Row className="justify-content-md-end mt-5 me-3 pt-3">
            <Col md={4} className="justify-content-md-end" >
                <h1 className="pb-3">Login</h1>

                <Form onSubmit={handleSubmit}>
                    <Alert
                        dismissible
                        show={show}
                        onClose={() => setShow(false)}
                        variant="danger">
                        {errorMessage}
                    </Alert>
                    <Form.Group className="mb-3" controlId="username">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            value={username} placeholder="Write here your email"
                            onChange={(ev) => setUsername(ev.target.value)}
                            required={true}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            value={password} placeholder="Write here your password"
                            onChange={(ev) => setPassword(ev.target.value)}
                            required={true} minLength={6}
                        />
                    </Form.Group>
                    <Button className="mt-3 items-color" type="submit">Login</Button>
                </Form>
            </Col>
        </Row>
    )
}

function LogoutButton(props) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        //await API.logout();
        // clean up everything
        //props.setUser(null);
    };

    return (
        <Button variant="outline-light" onClick={() => {
            navigate('/');
            handleLogout();
        }}>Logout</Button>
    )
}

function LoginButton(props) {
    const navigate = useNavigate();
    return (
        <Button variant="outline-light" onClick={() => navigate('/login')}>Login</Button>
    )
}

function LoginLayout() {
    return (
        <>
            <Row className="vh-100 login-padding-top">
                <Col md={6} mp={0} className="below-nav">
                    <LoginForm/>
                </Col>
                <Col md={6}>
                    <div className="blue-area">
                        {/*<Image src="http://localhost:3000/public/logo192.png" rounded />*/}
                        {/*<Image src="logo192.png" rounded />*/}
                        <img src={thesisicon} alt="My Image" style={{width:"5rem",height:"5rem"}} />
                    </div>
                </Col>
            </Row>
        </>
    );
}
export { LoginForm, LogoutButton, LoginButton, LoginLayout };

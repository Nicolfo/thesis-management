import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import Select from 'react-select';
import makeAnimated from 'react-select/animated';
import React, {useContext, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import API from "../API/Api";
import {AuthContext} from "react-oauth2-code-pkce";
import toast, {Toaster} from 'react-hot-toast';


function StartRequest(props) {
    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role !== "STUDENT")
        navigate("/notAuthorized");

    const animatedComponents = makeAnimated();
    const [title, setTitle] =  useState("");
    const [description, setDescription] = useState("");
    const [teacherList, setTeacherList] = useState([]);
    const [optionsSupervisors, setOptionsSupervisors] = useState([]);
    const [selectedSupervisor, setSelectedSupervisor] = useState({});
    const [selectedSupervisors, setSelectedSupervisors] = useState([]);
    const [isValidTitle, setIsValidTitle] = useState(true);
    const [isValidDescription, setIsValidDescription] = useState(true);
    const [isValidSupervisor, setIsValidSupervisor] = useState(true);
    const [validated, setValidated] = useState(false);


    const getAllTeachers = async () => {
        try {
            const teachers = await API.getAllTeachers(props.user.token);
            setTeacherList(teachers);

            let supervisors = [];
            teachers.forEach( (t) => {
                let elem= {label: `${t.surname} ${t.name}`, value: t.id};
                supervisors.push(elem);
            } );
            setOptionsSupervisors(supervisors);

        } catch (err) {
            console.error("UseEffect error", err);
        }
    }


    const clearFields = () => {
        setValidated(false);
        setTitle("");
        setDescription("");
        setSelectedSupervisor({});
        setSelectedSupervisors([]);
    }


    useEffect(() => {
        getAllTeachers();
    }, [])



    const startRequest = (request) => {
        toast.promise(
            (async () => {
                await API.startRequest(request, props.user.token);
                props.setSent(true);
                clearFields();
                return "Thesis request successfully sent";
            })(),
            {
                loading: 'Sending...',
                success: () => {
                    return <strong>Thesis request successfully sent</strong>
                },
                error: (error) => {
                    if (error && error.detail) {
                        return <strong>{error.detail}</strong>;
                    } else {
                        return <strong>An error occurred while sending the request</strong>;
                    }
                }
            });
    }

    const handleSubmit = (ev) => {
        ev.preventDefault();

        const valid = isValidDescription && isValidTitle && isValidSupervisor;

        if (ev.currentTarget.checkValidity() === false || !valid) {
            if (selectedSupervisor.value === undefined)
                setIsValidSupervisor(false);

            ev.stopPropagation();
        }
        else if (selectedSupervisor.value === undefined) {
            setIsValidSupervisor(false);
            ev.stopPropagation();
        }
        else {
            let request = {
                title: title,
                supervisor: selectedSupervisor.value,
                coSupervisors: teacherList.filter( (t) => selectedSupervisors.some( (s) => s.value === t.id) ).map((t) => t.id),
                description: description
            }

            startRequest(request);
        }

        setValidated(true);
    }


    return (
        <>
            <Card style={{"marginTop": "0.5", "marginBottom": "2rem"}}>
                <Form validated={validated} onSubmit={handleSubmit} noValidate>
                    <Card.Header as="h1" style={{"textAlign": "center"}} className="py-3">
                        Insert thesis request fields
                    </Card.Header>

                    <Card.Body>
                        {/* TITLE */}
                        <Row>
                            <Form.Group>
                                <Form.Floating>
                                    <Form.Control
                                        disabled={props.sent}
                                        required
                                        style={{borderRadius: "25px"}}
                                        type="text"
                                        placeholder="Title"
                                        value={title}
                                        isInvalid={!isValidTitle}
                                        onChange={ (ev) => {
                                            if ((ev.target.value.trim()).length > 0 ) {
                                                setTitle(ev.target.value);
                                                setIsValidTitle(true);
                                            } else {
                                                setTitle(ev.target.value);
                                                setIsValidTitle(false);
                                            }
                                        }
                                        }
                                    />
                                    <label htmlFor="floatingTitle" > Title </label>
                                    <Form.Control.Feedback type="invalid"> Please choose a title </Form.Control.Feedback>
                                </Form.Floating>
                            </Form.Group>
                        </Row>
                        {/* DESCRIPTION */}
                        <Row style={{"marginTop": "1rem"}} >
                            <Form.Floating>
                                <Form.Control
                                    disabled={props.sent}
                                    required
                                    as="textarea"
                                    type="text"
                                    style={{"height": "100px", borderRadius: "20px"}}
                                    placeholder="Description"
                                    value={description}
                                    isInvalid={!isValidDescription}
                                    onChange={ (ev) => {
                                        if ((ev.target.value.trim()).length > 0 ) {
                                            setDescription(ev.target.value);
                                            setIsValidDescription(true);
                                        } else {
                                            setDescription(ev.target.value);
                                            setIsValidDescription(false);
                                        }
                                    }
                                    }
                                />
                                <label htmlFor="floatingKnowledge" style={{"marginLeft": "0.5rem"}}> Description </label>
                                <Form.Control.Feedback type="invalid"> Please provide a description for the thesis request </Form.Control.Feedback>
                            </Form.Floating>
                        </Row>
                        {/* SUPERVISOR & CO-SUPERVISORS */}
                        <Row style={{"marginTop": "1rem"}} >
                            <Form.Group as={Col} >
                                <Form.Label style={props.sent ? {marginLeft: "0.3rem", color: "dimgray"} : {marginLeft: "0.3rem"}}> Supervisor </Form.Label>
                                <Select
                                    isDisabled={props.sent}
                                    options={optionsSupervisors}
                                    value={selectedSupervisor}
                                    onChange={ev => {setIsValidSupervisor(true); setSelectedSupervisor(ev)}}
                                    theme={(theme) => ({
                                        ...theme,
                                        borderRadius: 25,
                                        colors: {
                                            ...theme.colors,
                                            primary25: '#FED8B1',
                                            primary: '#FC7A08',
                                        },
                                    })}
                                />
                                { !isValidSupervisor &&
                                    <Form.Label style={{"color": "red"}}> Please select the supervisor <FontAwesomeIcon icon="fa-solid fa-circle-exclamation"/> </Form.Label>
                                }
                                <Form.Control.Feedback type="invalid"> Please select the supervisor </Form.Control.Feedback>
                            </Form.Group>
                            <Form.Group as={Col} >
                                <Form.Label style={props.sent ? {marginLeft: "0.3rem", color: "dimgray"} : {marginLeft: "0.3rem"}}> Co-supervisors <em style={{"color": "dimgray"}}> (optional) </em> </Form.Label>
                                <Select
                                    isDisabled={props.sent}
                                    options={optionsSupervisors}
                                    value={selectedSupervisors}
                                    onChange={setSelectedSupervisors}
                                    theme={(theme) => ({
                                        ...theme,
                                        borderRadius: 25,
                                        colors: {
                                            ...theme.colors,
                                            primary25: '#FED8B1',
                                            primary: '#FC7A08',
                                        },
                                    })}
                                    isMulti
                                    closeMenuOnSelect={false}
                                    components={animatedComponents}
                                />
                            </Form.Group>
                        </Row>
                    </Card.Body>

                    <Card.Footer style={{"textAlign": "center"}}>
                        { !props.sent ?
                            <Button variant="outline-primary" type="submit">
                                <FontAwesomeIcon icon="fa-solid fa-share-from-square" /> Send thesis request
                            </Button>
                            :
                            <Button variant="outline-dark" onClick={() => navigate('/browseDecisions')}>
                                <FontAwesomeIcon icon={"chevron-left"}/> Go back
                            </Button>
                        }
                    </Card.Footer>
                </Form>
            </Card>
            <Toaster
                position="top-right"
                containerClassName="mt-5"
                reverseOrder={false}
            />
        </>
    );
}


export default StartRequest;
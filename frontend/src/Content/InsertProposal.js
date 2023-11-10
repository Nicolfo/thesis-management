import {Alert, Button, Card, Col, Form, Row} from "react-bootstrap";
import { MultiSelect } from "react-multi-select-component";
import {useEffect, useState} from "react";
import dayjs from "dayjs";
import API from "../API/API";


function InsertProposal() {

    const [title, setTitle] =  useState("");
    const [level, setLevel] =  useState("");
    const [notes, setNotes] =  useState("");
    const [knowledge, setKnowledge] = useState("");
    const [description, setDescription] = useState("");
    const [date, setDate] = useState("");
    const [typeList, setTypeList] = useState([""]);
    const [keywordsList, setKeywordsList] = useState([""]);
    const [optionsSupervisors, setOptionsSupervisors] = useState([]);
    const [selectedSupervisors, setSelectedSupervisors] = useState([]);
    const [alert, setAlert] = useState(false);
    const [validated, setValidated] = useState(false);


    useEffect(() => {
        const getAllTeacherGroups = async () => {
            try{
                const
            }
        }
    }, [])

    const addType = () => {
        setTypeList([...typeList, ""] );
    }

    const removeType = (index) => {
        const list = [...typeList];
        list.splice(index, 1);
        setTypeList(list);
    }

    const changeType = (ev, index) => {
        const list = [...typeList];
        list[index] = ev.target.value;
        setTypeList(list);
    }

    const addKeyword = () => {
        setKeywordsList([...keywordsList, ""] );
    }

    const removeKeyword = (index) => {
        const list = [...keywordsList];
        list.splice(index, 1);
        setKeywordsList(list);
    }

    const changeKeyword = (ev, index) => {
        const list = [...keywordsList];
        list[index] = ev.target.value;
        setKeywordsList(list);
    }



    const insertProposal = (proposal) => {
        API.insertProposal(proposal)
            .then(() => setAlert(true))
            .catch((err) => console.log(err))
    }

    const handleSubmit = (ev) => {
        ev.preventDefault();

        if (ev.currentTarget.checkValidity() === false)
            ev.stopPropagation();
        else {
            const proposal = {
                title: title,
                keywords: keywordsList.join(", "),
                type: typeList.join(", "),
                description: description,
                requiredKnowledge: knowledge,
                notes: notes,
                level: level,
                expiration: dayjs(date).format("YYYY-MM-DD")
            }

            insertProposal(proposal);
        }

        setValidated(true);
    }


    return (
        <Card style={{"marginTop": "1rem", "marginBottom": "2rem"}}>
            <Form validated={validated} onSubmit={handleSubmit} noValidate>
                <Card.Header as="h3" style={{"textAlign": "center"}}> Insert proposal fields </Card.Header>

                <Card.Body>
                    {/* TITLE */}
                    <Row >
                        <Form.Group >
                            <Form.Label> Title </Form.Label>
                            <Form.Control
                                required
                                type="text"
                                placeholder="Title"
                                value={title}
                                onChange={ev => setTitle(ev.target.value)}
                            />
                            <Form.Control.Feedback type="invalid"> Please choose a title </Form.Control.Feedback>
                        </Form.Group>
                    </Row>
                    {/* SUPERVISOR & CO-SUPERVISORS */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} >
                            <Form.Label> Supervisor </Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="Inserire info del teacher loggato"
                                disabled
                                readOnly
                            />
                        </Form.Group>
                        <Form.Group as={Col} >
                            <Form.Label> Co-supervisors </Form.Label>
                            <MultiSelect
                                options={optionsSupervisors}
                                value={selectedSupervisors}
                                onChange={setSelectedSupervisors}
                                labelledBy="Select"
                            />
                        </Form.Group>
                    </Row>
                    {/* TYPE & KEYWORDS & LEVEL */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} >
                            <Form.Label> Thesis type </Form.Label>
                            {typeList.map( (singleType, index) => (
                                <>
                                    <Row style={{"marginBottom": "0.5rem"}}>
                                        <Col>
                                            <Form.Control
                                                required
                                                type="text"
                                                placeholder="Type"
                                                value={singleType}
                                                onChange={ (ev) => changeType(ev, index) }
                                            />
                                            <Form.Control.Feedback type="invalid"> Please write the type </Form.Control.Feedback>
                                        </Col>
                                        <Col>
                                            {typeList.length !== 1 &&
                                                <Button variant="danger" size="sm" onClick={() => removeType(index)}> X </Button>
                                            }
                                        </Col>
                                    </Row>
                                    {typeList.length - 1 === index &&
                                        <Button variant="success" size="sm" onClick={addType}> Add type </Button>
                                    }
                                </>
                            ))}
                            </Form.Group>
                        <Form.Group as={Col} >
                            <Form.Label> Keywords </Form.Label>
                            {keywordsList.map( (singleKeyword, index) => (
                                <>
                                    <Row style={{"marginBottom": "0.5rem"}}>
                                        <Col>
                                            <Form.Control
                                                required
                                                type="text"
                                                placeholder="Keyword"
                                                value={singleKeyword}
                                                onChange={ (ev) => changeKeyword(ev, index) }
                                            />
                                            <Form.Control.Feedback type="invalid"> Please write the keyword </Form.Control.Feedback>
                                        </Col>
                                        <Col>
                                            {keywordsList.length > 1 &&
                                                <Button variant="danger" size="sm" onClick={() => removeKeyword(index)}> X </Button>
                                            }
                                        </Col>
                                    </Row>
                                    {keywordsList.length - 1 === index &&
                                        <Button variant="success" size="sm" onClick={addKeyword}> Add keyword </Button>
                                    }
                                </>
                            ))}
                        </Form.Group>
                        <Form.Group as={Col} >
                            <Form.Label> Level </Form.Label>
                            <Form.Select value={level} onChange={ev => setLevel(ev.target.value)} >
                                <option value="BSc"> BSc </option>
                                <option value="MSc"> MSc </option>
                            </Form.Select>
                        </Form.Group>
                    </Row>
                    {/* NOTES & REQUIRED KNOWLEDGE & DESCRIPTION */}
                    <Row style={{"marginTop": "2rem"}} >
                        <Form.Floating>
                            <Form.Control
                                as="textarea"
                                type="text"
                                placeholder="Notes"
                                value={notes}
                                onChange={ev => setNotes(ev.target.value)}
                            />
                            <label htmlFor="floatingNotes" style={{"marginLeft": "0.5rem"}}> Notes </label>
                        </Form.Floating>
                    </Row>
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Floating>
                            <Form.Control
                                required
                                as="textarea"
                                type="text"
                                placeholder="Required knowledge"
                                value={knowledge}
                                onChange={ev=> setKnowledge(ev.target.value)}
                            />
                            <label htmlFor="floatingKnowledge" style={{"marginLeft": "0.5rem"}}> Required knowledge </label>
                            <Form.Control.Feedback type="invalid"> Please provide the required knowledge for the thesis </Form.Control.Feedback>
                        </Form.Floating>
                    </Row>
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Floating>
                            <Form.Control
                                required
                                as="textarea"
                                type="text"
                                placeholder="Description"
                                value={description}
                                onChange={ev=> setDescription(ev.target.value)}
                            />
                            <label htmlFor="floatingKnowledge" style={{"marginLeft": "0.5rem"}}> Description </label>
                            <Form.Control.Feedback type="invalid"> Please provide a description for the thesis </Form.Control.Feedback>
                        </Form.Floating>
                    </Row>
                    {/* EXPIRATION DATE & CDS */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} style={{"marginTop": "1rem"}} >
                            <Form.Floating>
                                <Form.Control
                                    required
                                    type="date"
                                    value={date}
                                    min={dayjs().format('YYYY-MM-DD')}
                                    onChange={ev => setDate(ev.target.value)}
                                />
                                <label htmlFor="floatingDate"> Expiration date </label>
                                <Form.Control.Feedback type="invalid"> Please choose a date </Form.Control.Feedback>
                            </Form.Floating>
                        </Form.Group>
                        <Form.Group as={Col} >
                            <Form.Label> CDS </Form.Label>
                            <Form.Select>

                            </Form.Select>
                        </Form.Group>
                    </Row>
                </Card.Body>

                <Card.Footer style={{"textAlign": "center"}}>
                    <Button variant="primary" type="submit"> Publish thesis proposal </Button>
                    {alert &&
                        <Alert variant="success" onClose={() => setAlert(false)} dismissible > Insert api successful </Alert>
                    }
                </Card.Footer>
            </Form>
        </Card>
    );
}

export default InsertProposal;
import {Alert, Button, Card, Col, Form, Row} from "react-bootstrap";
import { MultiSelect } from "react-multi-select-component";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import dayjs from "dayjs";
import API from "../API/API";


function InsertUpdateProposal(props) {
    const { proposalID } = useParams();

    // If edit then update
    const edit = proposalID && props.proposals.find( (p) => p.id === parseInt(proposalID));
    //
    const [supervisor, setSupervisor] = useState("");
    const [title, setTitle] =  useState(edit ? edit.title : "");
    const [level, setLevel] =  useState(edit ? edit.level : "");
    const [notes, setNotes] =  useState(edit ? edit.notes : "");
    const [knowledge, setKnowledge] = useState(edit ? edit.requiredKnowledge : "");
    const [description, setDescription] = useState(edit ? edit.description : "");
    const [date, setDate] = useState(edit ? edit.expiration.format("YYYY-MM-DD") : "");
    const [typeList, setTypeList] = useState([""]);
    const [keywordsList, setKeywordsList] = useState([""]);
    const [teacherList, setTeacherList] = useState([]);
    const [cdsList, setCdsList] = useState([""]);
    const [cds, setCds] = useState(edit ? edit.CdS : "");
    const [optionsSupervisors, setOptionsSupervisors] = useState([]);
    const [selectedSupervisors, setSelectedSupervisors] = useState([]);
    const [alert, setAlert] = useState(false);
    const [isValidTitle, setIsValidTitle] = useState(true);
    const [isValidDescription, setIsValidDescription] = useState(true);
    const [validated, setValidated] = useState(false);


    useEffect(() => {
        const getAllTeachersGroupsCds = async () => {
            try {
                const teachers = await API.getAllTeachersGroups();
                setTeacherList(teachers);

                let supervisors = {};
                (teachers).map( (t) => { supervisors['label'] = `${t.surname} ${t.name}`; supervisors['value'] = t.id } );
                setOptionsSupervisors(supervisors);

                const cds = await API.getAllCds();
                setCdsList(cds);

            } catch (err) {
                console.log(err);
            }
        };

        getAllTeachersGroupsCds();
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
                coSupervisors: teacherList.filter( (t) => selectedSupervisors.some( (s) => s.value === t.id)),
                // groups: inserire il gruppo del teacher loggato oltre a quelli dei co-supervisors
                keywords: keywordsList.join(", "),
                type: typeList.join(", "),
                description: description,
                requiredKnowledge: knowledge,
                notes: notes,
                level: level,
                expiration: dayjs(date).format("YYYY-MM-DD"),
                CdS: cds
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
                            { !isValidTitle &&
                                <Form.Control.Feedback type="invalid"> Please choose a title </Form.Control.Feedback>
                            }
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
                                as="textarea"
                                type="text"
                                placeholder="Required knowledge"
                                value={knowledge}
                                onChange={ev => setKnowledge(ev.target.value)}
                            />
                            <label htmlFor="floatingKnowledge" style={{"marginLeft": "0.5rem"}}> Required knowledge </label>
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
                            { !isValidDescription &&
                                <Form.Control.Feedback type="invalid"> Please provide a description for the thesis </Form.Control.Feedback>
                            }
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
                            <Form.Select value={cds} onChange={ev => setCds(ev.target.value)} >
                                {
                                    cdsList.map( (cds) => {
                                        return (<option value={cds}> {cds} </option>)
                                    })
                                }
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

export default InsertUpdateProposal;
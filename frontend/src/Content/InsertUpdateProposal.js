import {Alert, Button, Card, Col, Form, Row} from "react-bootstrap";
import { MultiSelect } from "react-multi-select-component";
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import dayjs from "dayjs";
import API from "../API/API2";


function InsertUpdateProposal(props) {
    const { proposalID } = useParams();
    const navigate=useNavigate();

    //
    const [supervisor, setSupervisor] = useState({});
    const [title, setTitle] =  useState("");
    const [level, setLevel] =  useState("");
    const [notes, setNotes] =  useState("");
    const [knowledge, setKnowledge] = useState("");
    const [description, setDescription] = useState("");
    const [date, setDate] = useState("");
    const [typeList, setTypeList] = useState([]);
    const [keywordsList, setKeywordsList] = useState([]);
    const [teacherList, setTeacherList] = useState([]);
    const [cdsList, setCdsList] = useState([]);
    const [optionsCds, setOptionsCds] = useState([]);
    const [selectedCds, setSelectedCds] = useState([]);
    const [optionsSupervisors, setOptionsSupervisors] = useState([]);
    const [selectedSupervisors, setSelectedSupervisors] = useState([]);
    const [alert, setAlert] = useState(false);
    const [isValidTitle, setIsValidTitle] = useState(true);
    const [isValidDescription, setIsValidDescription] = useState(true);
    const [isValidCds, setIsValidCds] = useState(true);
    const [validated, setValidated] = useState(false);

    const getAllTeachersGroupsCds = async () => {
        try {
            const teachers = await API.getAllTeachers(props.user.token);
            setTeacherList(teachers);

            let supervisors = [];
            teachers.forEach( (t) => {
                let elem= {label: `${t.surname} ${t.name}`, value: t.id};
                supervisors.push(elem);
            } );
            setOptionsSupervisors(supervisors);

            const cds = await API.getAllCds(props.user.token);
            setCdsList(cds);

            let CDS = [];
            cds.forEach( (c) => {
                let elem= {label: c, value: c};
                CDS.push(elem);

            } );
            setOptionsCds(CDS);

            const supervisor = await API.getByEmail(props.user.email,props.user.token);
            setSupervisor(supervisor);

            const proposals = await API.getProposalsByProf(props.user.token);
            // If edit then update
            let edit = proposalID && proposals.find( (p) => p.id === parseInt(proposalID));

            if (edit) {
                setTitle(edit.title);
                setLevel(edit.level);
                setNotes(edit.notes);
                setKnowledge(edit.requiredKnowledge);
                setDescription(edit.description);
                setDate(dayjs(edit.expiration).format("YYYY-MM-DD"));
                if(edit.type!=="")
                setTypeList(edit.type.split(", "));

                if(edit.keywords!=="")
                setKeywordsList(edit.keywords.split(", "));

                let CDS = [];
                edit.cdS.split(", ").forEach( (c) => {
                    let elem= {label: c, value: c};
                    CDS.push(elem);
                } );
                setSelectedCds(CDS);

                let supervisors = [];
                edit.coSupervisors.forEach( (t) => {
                    let elem= {label: `${t.surname} ${t.name}`, value: t.id};
                    supervisors.push(elem);
                } );
                setSelectedSupervisors(supervisors);
            }

        } catch (err) {
            console.error("UseEffect error", err);
        }
    };
    const clearFields=()=>{
        setTitle("");
        setLevel("");
        setNotes("");
        setKnowledge("");
        setDescription("");
        setDate("");
        setTypeList([]);
        setKeywordsList([]);
        setSelectedCds([]);
        setSelectedSupervisors([]);
    }
    useEffect(() => {
        if(!proposalID)
            clearFields();
        getAllTeachersGroupsCds();
    }, [proposalID])


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
        API.insertProposal(proposal,props.user.token)
            .then(() =>
                navigate("/teacher/proposals"))
            .catch((err) => console.log(err))
    }

    const updateProposal = (proposal) => {
        API.updateProposal(proposal,props.user.token)
            .then(() =>
                navigate("/teacher/proposals"))
            .catch((err) => console.log(err))
    }


    const handleSubmit = (ev) => {
        ev.preventDefault();

        if (ev.currentTarget.checkValidity() === false) {
            if (selectedCds.length === 0)
                setIsValidCds(false);

            ev.stopPropagation();
        }
        else if (selectedCds.length === 0) {
            setIsValidCds(false);
            ev.stopPropagation();
        }
        else {
            let proposal = {
                title: title,
                supervisorId: supervisor.id,
                coSupervisors: teacherList.filter( (t) => selectedSupervisors.some( (s) => s.value === t.id) ).map( (t) => t.id),
                keywords: keywordsList.join(", "),
                type: typeList.join(", "),
                description: description,
                requiredKnowledge: knowledge,
                notes: notes,
                level: level,
                expiration: dayjs(date).format("YYYY-MM-DD"),
                cds: (selectedCds.map( (c) => c.value)).join(", ")
            }
            if (proposalID) {
                proposal.id = proposalID;
                updateProposal(proposal);
            } else {

                insertProposal(proposal);
            }
        }

        setValidated(true);
    }


    return (
        <Card style={{"marginTop": "1rem", "marginBottom": "2rem"}}>
            <Form validated={validated} onSubmit={handleSubmit} noValidate>
                <Card.Header as="h3" style={{"textAlign": "center"}}>
                    { proposalID ?
                        "Update proposal fields"
                        :
                        "Insert proposal fields"
                    }
                </Card.Header>

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
                            <Form.Control.Feedback type="invalid"> Please choose a title </Form.Control.Feedback>
                        </Form.Group>
                    </Row>
                    {/* SUPERVISOR & CO-SUPERVISORS */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} >
                            <Form.Label> Supervisor </Form.Label>
                            <Form.Control
                                type="text"
                                placeholder={`${supervisor.surname} ${supervisor.name}`}
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
                                labelledBy="Select Co-Supervisors"
                            />
                        </Form.Group>
                    </Row>
                    {/* TYPE & KEYWORDS & LEVEL */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} >
                            <Form.Label> Thesis type </Form.Label>
                            { typeList.length === 0 &&
                                <>
                                    <Row style={{"marginBottom": "0.5rem", "marginLeft": "0.2rem", "width": "4.65rem"}}>
                                        <Button variant="success" size="sm" onClick={addType}> Add type </Button>
                                    </Row>
                                </>
                            }
                            {typeList.map( (singleType, index) => (
                                <>
                                    <Row style={{"marginBottom": "0.5rem"}}>
                                        <Col>
                                            <Form.Control
                                                required
                                                type="text"
                                                placeholder="Type"
                                                value={singleType}
                                                onKeyDown={ (ev) => { if (ev.key === ',') ev.preventDefault() }}
                                                onChange={ (ev) => changeType(ev, index) }
                                            />
                                            <Form.Control.Feedback type="invalid"> Please write the type </Form.Control.Feedback>
                                        </Col>
                                        <Col>
                                            {typeList.length !== 0 &&
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
                            { keywordsList.length === 0 &&
                                <>
                                    <Row style={{"marginBottom": "0.5rem", "marginLeft": "0.2rem", "width": "6.3rem"}}>
                                        <Button variant="success" size="sm" onClick={addKeyword}> Add keyword </Button>
                                    </Row>
                                </>
                            }
                            {keywordsList.map( (singleKeyword, index) => (
                                <>
                                    <Row style={{"marginBottom": "0.5rem"}}>
                                        <Col>
                                            <Form.Control
                                                required
                                                type="text"
                                                placeholder="Keyword"
                                                value={singleKeyword}
                                                onKeyDown={ (ev) => { if (ev.key === ',') ev.preventDefault() }}
                                                onChange={ (ev) => changeKeyword(ev, index) }
                                            />
                                            <Form.Control.Feedback type="invalid"> Please write the keyword </Form.Control.Feedback>
                                        </Col>
                                        <Col>
                                            {keywordsList.length !== 0 &&
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
                            <Form.Label> CdS </Form.Label>
                            <MultiSelect
                                options={optionsCds}
                                value={selectedCds}
                                onChange={setSelectedCds}
                                labelledBy="Select CdS"
                            />
                            { !isValidCds &&
                                <Form.Label style={{"color": "red"}}> Please select at least one CdS ! </Form.Label>
                            }
                        </Form.Group>
                    </Row>
                </Card.Body>

                <Card.Footer style={{"textAlign": "center"}}>
                    <Button variant="primary" type="submit">
                        { proposalID ?
                            "Update thesis proposal"
                            :
                            "Publish thesis proposal"
                        }
                    </Button>
                    {alert &&
                        <Alert variant="success" onClose={() => setAlert(false)} dismissible > Api successful </Alert>
                    }
                </Card.Footer>
            </Form>
        </Card>
    );
}

export default InsertUpdateProposal;
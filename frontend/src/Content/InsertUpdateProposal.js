import {Alert, Button, Card, Col, Form, Row} from "react-bootstrap";
import { MultiSelect } from "react-multi-select-component";
import {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import dayjs from "dayjs";
import API from "../API/Api";
import {AuthContext} from "react-oauth2-code-pkce";


function InsertUpdateProposal(props) {
    const { editProposalID, copyProposalID } = useParams();
    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if( !token )
        navigate("/notAuthorized");
    if(props.user && props.user.role==="STUDENT")
        navigate("/notAuthorized");

    const [supervisor, setSupervisor] = useState({});
    const [title, setTitle] =  useState("");
    const [level, setLevel] =  useState("Bachelor's");
    const [notes, setNotes] =  useState("");
    const [knowledge, setKnowledge] = useState("");
    const [description, setDescription] = useState("");
    const [date, setDate] = useState("");
    const [typeList, setTypeList] = useState([""]);
    const [keywordsList, setKeywordsList] = useState([""]);
    const [teacherList, setTeacherList] = useState([]);
    const [cdsList, setCdsList] = useState([]);
    const [optionsCds, setOptionsCds] = useState([]);
    const [selectedCds, setSelectedCds] = useState([]);
    const [optionsSupervisors, setOptionsSupervisors] = useState([]);
    const [selectedSupervisors, setSelectedSupervisors] = useState([]);
    const [alert, setAlert] = useState(false);
    const [isValidTitle, setIsValidTitle] = useState(true);
    const [isValidDescription, setIsValidDescription] = useState(true);
    const [isValidType, setIsValidType] = useState(true);
    const [isValidKeyword, setIsValidKeyword] = useState(true);
    const [isValidCds, setIsValidCds] = useState(true);
    const [validated, setValidated] = useState(false);
    const [cancel, setCancel] = useState(false);

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
            // If edit or copy, then populate the fields
            let editORCopy = ( (editProposalID && proposals.find( (p) => p.id === parseInt(editProposalID))) || (copyProposalID && proposals.find( (p) => p.id === parseInt(copyProposalID))) );

            if (editORCopy) {
                setTitle(editORCopy.title);
                setLevel(editORCopy.level);
                setNotes(editORCopy.notes);
                setKnowledge(editORCopy.requiredKnowledge);
                setDescription(editORCopy.description);
                setDate(dayjs(editORCopy.expiration).format("YYYY-MM-DD"));

                if(editORCopy.type !== "")
                    setTypeList(editORCopy.type.split(", "));

                if(editORCopy.keywords !== "")
                    setKeywordsList(editORCopy.keywords.split(", "));

                let CDS = [];
                editORCopy.cds.split(", ").forEach( (c) => {
                    let elem= {label: c, value: c};
                    CDS.push(elem);
                } );
                setSelectedCds(CDS);

                let supervisors = [];
                editORCopy.coSupervisors.forEach( (t) => {
                    let elem= {label: `${t.surname} ${t.name}`, value: t.id};
                    supervisors.push(elem);
                } );
                setSelectedSupervisors(supervisors);
            }

        } catch (err) {
            console.error("UseEffect error", err);
        }
    }

    const clearFields = ()=> {
        setTitle("");
        setLevel("Bachelor's");
        setNotes("");
        setKnowledge("");
        setDescription("");
        setDate("");
        setTypeList([""]);
        setKeywordsList([""]);
        setSelectedCds([]);
        setSelectedSupervisors([]);
    }

    useEffect(() => {
        if( (!editProposalID) || (!copyProposalID) )
            clearFields();
        getAllTeachersGroupsCds();
    }, [editProposalID, copyProposalID])


    const addType = () => {
        setTypeList([...typeList, ""] );
    }

    const removeType = (index) => {
        const list = [...typeList];
        list.splice(index, 1);
        setTypeList(list);
    }

    const changeType = (ev, index) => {
        if ((ev.target.value.trim()).length > 0 ) {
            const list = [...typeList];
            list[index] = ev.target.value;
            setTypeList(list);
            setIsValidType(true);
        } else {
            const list = [...typeList];
            list[index] = ev.target.value;
            setTypeList(list);
            setIsValidType(false);
        }
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
        if ((ev.target.value.trim()).length > 0 ) {
            const list = [...keywordsList];
            list[index] = ev.target.value;
            setKeywordsList(list);
            setIsValidKeyword(true);
        } else {
            const list = [...keywordsList];
            list[index] = ev.target.value;
            setKeywordsList(list);
            setIsValidKeyword(false);
        }
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

        const valid = isValidKeyword && isValidDescription && isValidType && isValidTitle && isValidCds;

        if (ev.currentTarget.checkValidity() === false || !valid) {
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
            if (editProposalID) {
                proposal.id = editProposalID;
                updateProposal(proposal);
            } else
                insertProposal(proposal);
        }

        setValidated(true);
    }

    function cancelButt(){
        setCancel(true);
    }

    return (
        <Card style={{"marginTop": "0.5", "marginBottom": "2rem"}}>
            <Form validated={validated} onSubmit={handleSubmit} noValidate>
                <Card.Header as="h1" style={{"textAlign": "center"}} className="py-3">
                    { editProposalID ?
                        "Update proposal"
                        :
                        "Insert proposal"
                    }
                </Card.Header>

                <Card.Body>
                    {/* TITLE */}
                    <Row>
                        <Form.Group>
                            <Form.Floating>
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
                                <label htmlFor="floatingTitle" > Title </label>
                                <Form.Control.Feedback type="invalid"> Please choose a title </Form.Control.Feedback>
                            </Form.Floating>
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
                            <Form.Label> Co-supervisors <em style={{"color": "dimgray"}}> (optional) </em> </Form.Label>
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
                        <Col lg={4}>
                            <Form.Group className="ms-2">
                                <Form.Label> Thesis types </Form.Label>
                                {typeList.map( (singleType, index) => (
                                    <>
                                        <Row style={{"marginBottom": "0.5rem"}}>
                                            <Col>
                                                <Form.Control
                                                    required
                                                    type="text"
                                                    placeholder="Type"
                                                    // style={{"width": "250px"}}
                                                    value={singleType}
                                                    onKeyDown={ (ev) => { if (ev.key === ',') ev.preventDefault() }}
                                                    onChange={ (ev) => changeType(ev, index) }
                                                />
                                                <Form.Control.Feedback type="invalid"> Please write the type </Form.Control.Feedback>
                                            </Col>
                                            <Col>
                                                {typeList.length > 1 &&
                                                    <Button variant="link" size="sm" onClick={() => removeType(index)}> <FontAwesomeIcon icon="fa-regular fa-circle-xmark" size="xl" style={{color: "#f50000",}} /> </Button>                                            }
                                            </Col>
                                        </Row>
                                        {typeList.length - 1 === index &&
                                            <Button variant="info" size="sm" onClick={addType}> <FontAwesomeIcon icon="fa-solid fa-plus" /> Add type </Button>
                                        }
                                    </>
                                ))}
                            </Form.Group>
                        </Col>
                        <Col lg={4}>
                            <Form.Group className="ms-2">
                                <Form.Label> Thesis keywords </Form.Label>
                                {keywordsList.map( (singleKeyword, index) => (
                                    <>
                                        <Row style={{"marginBottom": "0.5rem"}}>
                                            <Col>
                                                <Form.Control
                                                    required
                                                    type="text"
                                                    placeholder="Keyword"
                                                    // style={{"width": "200px"}}
                                                    value={singleKeyword}
                                                    onKeyDown={ (ev) => { if (ev.key === ',') ev.preventDefault() }}
                                                    onChange={ (ev) => changeKeyword(ev, index) }
                                                />
                                                <Form.Control.Feedback type="invalid"> Please write the keyword </Form.Control.Feedback>
                                            </Col>
                                            <Col>
                                                {keywordsList.length > 1 &&
                                                    <Button variant="link" size="sm" onClick={() => removeKeyword(index)}> <FontAwesomeIcon icon="fa-regular fa-circle-xmark" size="xl" style={{color: "#f50000",}} /> </Button>
                                                }
                                            </Col>
                                        </Row>
                                        {keywordsList.length - 1 === index &&
                                            <Button variant="info" size="sm" onClick={addKeyword}> <FontAwesomeIcon icon="fa-solid fa-plus" /> Add keyword </Button>
                                        }
                                    </>
                                ))}
                            </Form.Group>
                        </Col>
                        <Col lg={4}>
                            <Form.Group className="ms-2">
                                <Form.Label> Thesis level </Form.Label>
                                <Form.Check
                                    type="radio"
                                    name="level"
                                    label="Bachelor's"
                                    value="Bachelor's"
                                    checked={level === "Bachelor's"}
                                    onChange={ev => setLevel(ev.target.value)}
                                />
                                <Form.Check
                                    type="radio"
                                    name="level"
                                    label="Master's"
                                    value="Master's"
                                    checked={level === "Master's"}
                                    onChange={ev => setLevel(ev.target.value)}
                                />
                            </Form.Group>
                        </Col>
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
                            <label htmlFor="floatingNotes" style={{"marginLeft": "0.5rem"}}> Notes <em style={{"color": "dimgray"}}> (optional) </em> </label>
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
                            <label htmlFor="floatingKnowledge" style={{"marginLeft": "0.5rem"}}> Required knowledge <em style={{"color": "dimgray"}}> (optional) </em> </label>
                        </Form.Floating>
                    </Row>
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Floating>
                            <Form.Control
                                required
                                as="textarea"
                                type="text"
                                style={{"height": "100px"}}
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
                    {/* CDS & EXPIRATION DATE */}
                    <Row style={{"marginTop": "1rem"}} >
                        <Form.Group as={Col} >
                            <Form.Label> CdS </Form.Label>
                            <MultiSelect
                                options={optionsCds}
                                value={selectedCds}
                                onChange={ev => {setIsValidCds(true); setSelectedCds(ev)}}
                                labelledBy="Select CdS"
                            />
                            { !isValidCds &&
                                <Form.Label style={{"color": "red"}}> Please select at least one CdS <FontAwesomeIcon icon="fa-solid fa-circle-exclamation"/> </Form.Label>
                            }
                        </Form.Group>
                        <Col sm={7}>
                            <Form.Group style={{"marginTop": "1rem"}} >
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
                        </Col>
                    </Row>
                </Card.Body>

                <Card.Footer style={{"textAlign": "center"}}>
                    <Button variant="outline-primary" type="submit">
                        { editProposalID ?
                            <><FontAwesomeIcon icon="fa-solid fa-check" /> Update thesis proposal</>
                            :
                            <><FontAwesomeIcon icon={"upload"} /> Publish thesis proposal</>
                        }
                    </Button>


                    <Button variant="outline-danger" style={{marginLeft: "1rem"}} onClick={() => {navigate('/teacher/proposals');}}>
                        <FontAwesomeIcon icon="fa-solid fa-xmark" /> Cancel
                    </Button>

                </Card.Footer>
            </Form>
        </Card>
    );
}


export default InsertUpdateProposal;
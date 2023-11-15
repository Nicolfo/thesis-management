import Table from 'react-bootstrap/Table';
import {Button, Col, Form, FormGroup, FormLabel, Row} from "react-bootstrap";
import {useNavigate} from 'react-router-dom';
import {useEffect, useState} from "react";

function ProposalList(props){
    const navigate = useNavigate();


    const shortProposalSchema = ["Id", "Title", "Supervisor","Keywords", "Level", "cdS","Expiration"]

    let shortProposal = props.listOfProposal.map((e,i) => {
        return {id: e.id, title: e.title, supervisor: e.supervisor.surname, keywords: e.keywords, level: e.level, cdS:e.cdS ,expiration: e.expiration};

    })

    return (

        <div>
            <h2>List of Active Proposal</h2>
            <SearchBar listOfSupervisors={props.listOfSupervisors} clickOnProposal={props.clickOnProposal} filterProposals={props.filterProposals}></SearchBar>


            {shortProposal.map((proposal, index)=>{
                return(
                    <Table >
                        <thead >
                            <tr>
                                {
                                    shortProposalSchema.map((e,i) => {
                                        return(
                                            <th key={e}>{e}</th>
                                        );

                                    })
                                }

                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                        {Array.from(Object.values(proposal)).map((e,i) => {

                            if (Array.isArray(e)){
                                return (<td key={i}>{e.map((el,ind) => {
                                    return(

                                       el+ " "

                                    );
                                })}</td>)

                            }
                            else if(i ==6){
                                return(<td key={i}>{e.slice(0,10)}</td>)

                            }
                            else{
                            return( <td key={i}>{e}</td>

                            );
                        }})}
                                <Button onClick={()=> {props.setProposalSelected(index); navigate('/see-proposal')}}>See </Button>
                            </tr>

                        </tbody>
                    </Table>

                );

            })}
        </div>
    );
}



function SearchBar(props){
    //let professorsList = ["Andrea", "Lia Morra", "Simone Inzaghi", "Marco", "Giardini", "Feroce"]
    let professorsList = props.listOfSupervisors.map((prof,i) => {return (prof.surname)})

    let typeList = ["Bachelor", "Master"]
    const [professor, setProfessor] = useState("All");
    const [type, setType] = useState("All")

    useEffect(() => {
        setProfessor("All")
        setType("All")
    }, [props.clickOnProposal]);

    return(
        <FormGroup>
            <Row>
                <Col>
                    <FormLabel>Professor:</FormLabel>
                    <Form.Select name="type" value={professor} onChange={(e) => setProfessor(e.target.value)}>
                        <option>All</option>

                        {professorsList.map((e,i) => {

                            return(
                                <option> {e}</option>
                            )
                        })}


                    </Form.Select>

                </Col>

                <Col>
                    <FormLabel>Type:</FormLabel>
                    <Form.Select name="type" value={type} onChange={(e) => setType(e.target.value)}>
                        <option>All</option>
                        {typeList.map((e,i) => {

                            return(
                                <option> {e}</option>
                            )
                        })}


                    </Form.Select>

                </Col>

                <Col><Button onClick={()=> {let arr = [professor, type]; props.filterProposals(arr);}}>Search</Button></Col>


            </Row>


        </FormGroup>

    );


}
export default ProposalList;
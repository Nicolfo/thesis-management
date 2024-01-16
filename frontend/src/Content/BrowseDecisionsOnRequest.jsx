import {useContext, useEffect, useState} from "react";
import {Badge, Card, Col, Row, Table} from "react-bootstrap";
import API from "../API/Api";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";
import {TableRow} from "./BrowseDecisions"

function BrowseDecisionsOnRequest(props) {
    const {token} = useContext(AuthContext);

    const [proposalOnRequest, setproposalOnRequest] = useState([]);


    const navigate = useNavigate();

    useEffect(() => {
        if (!token)
            navigate("/notAuthorized");
        if (props.user && props.user.role === "TEACHER")
            navigate("/notAuthorized");
        const getApplicationsByStudent = async () => {
            if (!props.user)
                return;

            try {
                let applicationsOnReq = await API.getProposalOnRequestByStudent(props.user.token);
                setproposalOnRequest(applicationsOnReq);

            } catch (err) {
                console.error("Error fetching applications:", err);
            }
        };

        getApplicationsByStudent();
    }, [props.user]);


    return (
        <>
            <Card style={{"marginTop": "1rem", "marginBottom": "2rem", "marginRight": "1rem"}}>
                <Card.Header as="h1" style={{"textAlign": "center"}} className="py-3">
                    Your proposals on request
                </Card.Header>

                {proposalOnRequest.length > 0 ?


                    <Table>
                        <tbody>
                        {proposalOnRequest.map((application) => <TableRow key={application.id} application={application} proposalOnRequest={proposalOnRequest} navigate={navigate}/>)}
                        </tbody>
                    </Table>

                    :
                    <Card.Body style={{"textAlign": "center"}} className="mt-4">
                        <strong>You have no proposals on request yet</strong>
                    </Card.Body>
                }



            </Card>
        </>);
}




export default BrowseDecisionsOnRequest;
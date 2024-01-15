import {useContext, useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {AuthContext} from "react-oauth2-code-pkce";
import API from "../API/Api";

function ProposalViewLayout(props) {

    const [proposal, setProposal] = useState(null);

    const navigate = useNavigate();
    const {token} = useContext(AuthContext);
    if (!token)
        navigate("/notAuthorized");
    if (props.user && props.user.role === "STUDENT")
        navigate("/notAuthorized");

    const proposalId = new URLSearchParams(useLocation().search).get("applicationId");

    const getProposalById = async () => {
        if (props.user && props.user.token)
            try {
                const proposal = await API.getProposalById(proposalId);
                setProposal(proposal);
            } catch (error) {
                console.error("Error fetching proposals:", error);
            }
    }

    useEffect(() => {
        if (!props.user || props.user.role !== "TEACHER")
            return;
        getProposalById();
    }, [props.user]);

    return (
        <>

        </>
    );
}


export default ProposalViewLayout;
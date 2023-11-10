import { useState } from "react";
import { useEffect } from "react";
import API from "../API/API";
import { Table } from "react-bootstrap";

export default function BrowseProposalsContent() {

    const [proposalList, setProposalList] = useState([]);

    

    useEffect(() => {
        const getProposalList = async () => {
            const list = await API.getAllProposals();
            setProposalList(list);
        }
        getProposalList();
    }, []);

    return (
        <>
            <h4>Your thesis proposals</h4>
            <Table striped hover>
                <thead>
                    <tr>
                    <th>Supervisor</th>
                    <th>Title</th>
                    <th>Level</th>
                    <th>CdS</th>
                    </tr>
                </thead>
                <tbody>
                    { proposalList.map(proposal => <ProposalRow proposal={proposal} />) }
                </tbody>
                </Table>
        </>
    );

}


function ProposalRow({ proposal }) {
    return (
        <tr>
            <td>{ proposal.supervisor.surname + " " + proposal.supervisor.name }</td>
            <td>{ proposal.title }</td>
            <td>{ proposal.level }</td>
            <td>{ proposal.cdS }</td>
        </tr>
    )
}
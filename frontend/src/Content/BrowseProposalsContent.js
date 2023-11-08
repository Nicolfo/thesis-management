import { useState } from "react";
import { useEffect } from "react";
import API from "../API/API";

function BrowseProposalsContent() {

    const [proposalList, setProposalList] = useState([]);

    const getProposalList = async () => {
        const list = await API.getAllProposals();
        setProposalList(list);
    }

    useEffect(() => getProposalList(), []);

    return (
        <>
            <h4>Thesis Proposals</h4>
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
                    <tr>
                    <td>1</td>
                    <td>Mark</td>
                    <td>Otto</td>
                    <td>@mdo</td>
                    </tr>
                    <tr>
                    <td>2</td>
                    <td>Jacob</td>
                    <td>Thornton</td>
                    <td>@fat</td>
                    </tr>
                    <tr>
                    <td>3</td>
                    <td colSpan={2}>Larry the Bird</td>
                    <td>@twitter</td>
                    </tr>
                </tbody>
                </Table>
        </>
    );

}

function ProposalsTable({ proposalList }) {

}

function ProposalRow({ proposal }) {

}
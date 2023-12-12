import {Button, Modal} from "react-bootstrap";
import API from "../API/Api";

function Warn(props) {
    return (
        <div
            className="modal show d-flex align-items-center justify-content-center vh-100"
        >
            <Modal.Dialog>
                <Modal.Header>
                    <Modal.Title> Warning!</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {props.archive ? <p>Do you want to archive this proposal?</p> :
                        <p>Do you want to delete this proposal?</p>}

                </Modal.Body>
                {props.archive ?

                    <Modal.Footer>
                        <Button variant="primary" onClick={() => {
                            props.setDeleting(false);
                            props.setArchive(false)
                        }}>Undo</Button>
                        <Button variant="danger" onClick={() => {
                            API.archiveProposal(props.deletingID, props.user.token).then(() => {
                                props.setDeleting(false);
                                props.setArchive(false);
                                props.getProposalList()
                            })
                        }}>Archive</Button>
                    </Modal.Footer>
                    :
                    <Modal.Footer>
                        <Button variant="primary" onClick={() => props.setDeleting(false)}>Undo</Button>
                        <Button variant="danger" onClick={() => {
                            if (props.archivedProposal) {
                                API.deleteProposal(props.deletingID, props.user.token).then(() => {
                                    props.setDeleting(false);
                                    props.doSearch()
                                })

                            } else {
                                API.deleteProposal(props.deletingID, props.user.token).then(() => {
                                    props.setDeleting(false);
                                    props.getProposalList()
                                })
                            }
                        }}>Delete</Button>
                    </Modal.Footer>

                }
            </Modal.Dialog>
        </div>
    );
}

export default Warn;
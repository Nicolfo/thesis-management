import {useNavigate,useLocation} from 'react-router-dom';
function SideBar(props){
    const path = useLocation().pathname;
    const navigate = useNavigate();

    const userIsTeacher = () => {
        return props.user && props.user.role === "TEACHER";
    }
    const userIsStudent = () => {
        return props.user && props.user.role === "STUDENT";
    }
    if(props.user)
    return (
        <div className="d-flex flex-column bg-light p-2 col-2">

                <ul className="nav nav-pills flex-column mb-auto nav-fill ">
                    {userIsStudent()&&<>
                    <li className="nav-item">
                        <button className={path==='/search-for-proposal' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/search-for-proposal'); props.searchForProposalClicked()}} >
                            Search for proposal
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={path==='/browseDecisions' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/browseDecisions')}} >
                            My applications decisions
                        </button>
                    </li>
                    </>
                    }
                    {
                        userIsTeacher() &&<>
                        <li className="nav-item">
                        <button className={path==='/teacher/proposals' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/teacher/proposals')}} >
                            My thesis proposals
                        </button>
                        </li>

                    <li className="nav-item">
                        <button className={path==='/insertProposal' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/insertProposal')}} >
                            Insert Proposal
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={path==='/teacher/application/browse'|| path==='/teacher/application/view' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/teacher/application/browse')}} >
                            My application proposals
                        </button>
                    </li>
                        </>
                    }

                </ul>
        </div>

    )

}


export default SideBar;
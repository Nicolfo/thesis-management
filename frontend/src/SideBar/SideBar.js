import {useNavigate,useLocation} from 'react-router-dom';
function SideBar(props){
    const path = useLocation().pathname;
    const navigate = useNavigate();

    return (
        <div className="d-flex flex-column bg-light p-2 col-2">

                <ul className="nav nav-pills flex-column mb-auto nav-fill ">
                    <li className="nav-item">
                        <button className={path==='/search-for-proposal' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/search-for-proposal'); props.searchForProposalClicked()}} >
                            Search for proposal
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={path==='/teacher/proposal/browse' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/teacher/proposal/browse')}} >
                            My thesis proposals
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={path==='/teacher/application/browse' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/teacher/application/browse')}} >
                            My application proposals
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={path==='/browseDecisions' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/browseDecisions')}} >
                            My applications decisions
                        </button>
                    </li>


                </ul>

        </div>

    )

}


export default SideBar;
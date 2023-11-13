import {useNavigate,useLocation} from 'react-router-dom';
function SideBar({ user }){
    const path = useLocation().pathname;
    const navigate = useNavigate();

    const userIsTeacher = () => {
        return user && user.role === "TEACHER";
    }

    return (
        <div className="d-flex flex-column bg-light p-2 col-2">

                <ul className="nav nav-pills flex-column mb-auto nav-fill ">
                    <li className="nav-item">
                        <button className={path==='/path-one' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/path-one')}} >
                            Path One (Edit SideBar.js)
                        </button>
                    </li>
                    {
                        userIsTeacher() &&
                        <li className="nav-item">
                        <button className={path==='/teacher/proposal/browse' ? "nav-link active link-light text-start":"nav-link link-dark text-start"} onClick={()=>{navigate('/teacher/proposal/browse')}} >
                            My thesis proposals
                        </button>
                        </li>
                    }
                </ul>

        </div>

    )

}


export default SideBar;
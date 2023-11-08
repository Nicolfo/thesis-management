import NavBar from "../NavBar/NavBar";
import SideBar from "../SideBar/SideBar";

function Navigation(props) {
    return (
        <>
            <NavBar user={props.user} realDate={props.realDate} applicationDate={props.applicationDate} updateApplicationDate={props.updateApplicationDate}>
            </NavBar>
            <SideBar>
            </SideBar>
        </>
    );
}
export default Navigation;
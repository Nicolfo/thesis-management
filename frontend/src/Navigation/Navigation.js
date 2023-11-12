import NavBar from "../NavBar/NavBar";

function Navigation(props) {
    return (
        <>
            <NavBar user={props.user} realDate={props.realDate} applicationDate={props.applicationDate} updateApplicationDate={props.updateApplicationDate}>
            </NavBar>
        </>
    );
}
export default Navigation;
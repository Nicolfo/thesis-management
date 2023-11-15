import NavBar from "../NavBar/NavBar";

function Navigation(props) {
    return (
        <>
            <NavBar user={props.user} setUser={props.setUser} realDate={props.realDate} applicationDate={props.applicationDate} updateApplicationDate={props.updateApplicationDate}>
            </NavBar>
        </>
    );
}
export default Navigation;
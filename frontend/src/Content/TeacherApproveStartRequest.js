import { useContext, useEffect } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useNavigate } from "react-router-dom";

function TeacherApproveStartRequestContent({ user }) {
    const navigate = useNavigate();
    const { token } = useContext(AuthContext);

    // Not authorized if not teacher
    useEffect(() => {
        if (!token || !user)
            navigate("/notAuthorized");
        if (user && user.role !== "TEACHER")
            navigate("/notAuthorized");
    }, []);
}
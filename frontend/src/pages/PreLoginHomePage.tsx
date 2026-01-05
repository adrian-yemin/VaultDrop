import PreLoginTopNav from "../components/TopNav/PreLoginTopNav.tsx";
import AnonymousUploadSection from "../components/AnonymousUpload/AnonymousUploadSection";
import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";

import { useEffect } from "react";

export default function PreLoginHomePage() {
    const { token } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (token) {
            navigate("/dashboard");
        }
    }, [token, navigate]);

    return (
        <>
            <PreLoginTopNav />
            <AnonymousUploadSection />
        </>
    );
}

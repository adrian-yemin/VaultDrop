import { useNavigate } from "react-router-dom";
import AuthCard from "../components/Authentication/AuthCard";
import { register as registerApi } from "../services/AuthService";

export default function RegisterPage() {
    const navigate = useNavigate();

    const handleRegister = async (email: string, password: string) => {
        await registerApi(email, password);
        navigate("/login");
    };

    return (
        <AuthCard
            title="Create an Account"
            submitText="Register"
            onSubmit={handleRegister}
        />
    );
}

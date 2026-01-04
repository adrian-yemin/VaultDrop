import { useNavigate } from "react-router-dom";
import AuthCard from "../components/Authentication/AuthCard";
import { login as loginApi } from "../services/AuthService";
import { useAuth } from "../auth/AuthContext";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleLogin = async (email: string, password: string) => {
        const res = await loginApi(email, password);

        login(res.data);

        navigate("/dashboard");
    };

    return (
        <AuthCard
            title="Login"
            submitText="Login"
            onSubmit={handleLogin}
        />
    );
}

import "./TopNav.css";
import { useNavigate } from "react-router-dom";

export default function AuthTopNav() {
    const navigate = useNavigate();

    const handleBack = () => {
        navigate("/");
    };

    return (
        <nav className="topnav">
            <div className="topnav-logo">VaultDrop</div>
            <div className="topnav-actions">
                <button
                    onClick={handleBack}
                    className="link-button"
                >
                    Back To Home
                </button>
            </div>
        </nav>
    );
}

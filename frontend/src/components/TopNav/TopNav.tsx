import "./TopNav.css";
import { useNavigate } from "react-router-dom";

export default function TopNav() {
    const navigate = useNavigate();

    return (
        <nav className="topnav">
            <div className="topnav-logo">VaultDrop</div>
            <div className="topnav-actions">
                <button onClick={() => navigate("/login")} className="link-button">Log in</button>
                <button onClick={() => navigate("/register")} className="primary-button">Sign up</button>
            </div>
        </nav>
    );
}

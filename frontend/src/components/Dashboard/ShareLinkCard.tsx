import type { ShareLinkDTO } from "../../types/ShareLink";
import "./Dashboard.css"

type Props = {
    link: ShareLinkDTO;
    onDelete: (token: string) => void;
};

export default function ShareLinkCard({ link, onDelete }: Props) {
    const fullUrl = `http://vaultdrop-load-balancer-549238236.us-east-1.elb.amazonaws.com/api/share/${link.token}`;

    const handleCopy = () => {
        navigator.clipboard.writeText(fullUrl);
    };

    return (
        <div className="dashboard-card">
            <div className="card-header">
                <span className="file-name">{link.fileName}</span>
                <div className="card-actions">
                    <button onClick={() => onDelete(link.token)}>Delete</button>
                </div>
            </div>
            <div className="card-body">
                <p>One-time use: {link.oneTimeUse ? "Yes" : "No"}</p>
                <p>Max downloads: {link.maxDownloads}</p>
                {link.expiresAt && <p>Expires at: {new Date(link.expiresAt).toLocaleString()}</p>}

                <div className="share-link-wrapper">
                    <input type="text" readOnly value={fullUrl} />
                    <button className="copy-button" onClick={handleCopy}>
                        Copy
                    </button>
                </div>
            </div>
        </div>
    );
}

import type { FileDTO } from "../../types/File";
import "./Dashboard.css"

type Props = {
    file: FileDTO;
    onDelete: (id: string) => void;
    onShare: (fileId: string) => void;
};

export default function FileCard({ file, onDelete, onShare }: Props) {
    return (
        <div className="dashboard-card">
            <div className="card-header">
                <span className="file-name">{file.originalFilename}</span>
                <div className="card-actions">
                    <button onClick={() => onShare(file.id)}>Share</button>
                    <button onClick={() => onDelete(file.id)}>Delete</button>
                </div>
            </div>
            <div className="card-body">
                <p>Uploaded: {new Date(file.uploadedAt).toLocaleString()}</p>
                <p>Size: {file.size} bytes</p>
            </div>
        </div>
    );
}

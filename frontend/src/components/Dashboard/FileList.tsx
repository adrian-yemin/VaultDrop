import FileCard from "./FileCard";
import type { FileDTO } from "../../types/File";

type Props = {
    files: FileDTO[];
    onDelete: (id: string) => void;
    onShare: (fileId: string) => void;
};

export default function FileList({ files, onDelete, onShare }: Props) {
    if (!files || files.length === 0) {
        return <p className="empty-message">You Have Not Uploaded Any Files Yet</p>;
    }

    return (
        <div className="file-list">
            {files.map((file) => (
                <FileCard
                    key={file.id}
                    file={file}
                    onDelete={onDelete}
                    onShare={onShare}
                />
            ))}
        </div>
    );
}


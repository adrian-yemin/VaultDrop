import ShareLinkCard from "./ShareLinkCard";
import type { ShareLinkDTO } from "../../types/ShareLink";

type Props = {
    links: ShareLinkDTO[];
    onDelete: (token: string) => void;
};

export default function ShareLinkList({ links, onDelete }: Props) {
    if (!links || links.length === 0) {
        return <p className="empty-message">You Have Not Created Any Share Links Yet</p>;
    }

    return (
        <div className="sharelink-list">
            {links.map((link) => (
                <ShareLinkCard key={link.token} link={link} onDelete={onDelete} />
            ))}
        </div>
    );
}


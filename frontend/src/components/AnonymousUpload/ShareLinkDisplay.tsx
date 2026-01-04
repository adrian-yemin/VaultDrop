type ShareLinkDisplayProps = {
    link: string;
};

export default function ShareLinkDisplay({ link }: ShareLinkDisplayProps) {
    const handleCopy = async () => {
        await navigator.clipboard.writeText(link);
    };

    return (
        <div className="share-link-card">
            <p className="share-link-text">Your share link:</p>
            <div className="share-link-wrapper">
                <input type="text" value={link} readOnly />
                <button
                    className="copy-button"
                    onClick={() => handleCopy()}
                >
                    Copy
                </button>
            </div>
        </div>
    );
}

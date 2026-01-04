type FileUploadBoxProps = {
    onUpload: () => void;
    onFileChange: (file: File | null) => void;
    disabled?: boolean;
};

export default function FileUploadBox({
                                          onUpload,
                                          onFileChange,
                                          disabled = false,
                                      }: FileUploadBoxProps) {
    return (
        <div className="upload-box">
            <input
                type="file"
                onChange={(e) =>
                    onFileChange(e.target.files ? e.target.files[0] : null)
                }
            />
            <button
                type="button"
                className="primary-button"
                onClick={onUpload}
                disabled={disabled}
            >
                Upload
            </button>
        </div>
    );
}

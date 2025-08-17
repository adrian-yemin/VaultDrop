import { useState } from 'react'
import './App.css'

function App() {
  const [file, setFile] = useState(null)
  const [uploadLink, setUploadLink] = useState(null);
  return (
    <div>
      <h1>VaultDrop</h1>
      <input type='file' onChange={(e) => setFile(e.target.files[0])}></input>
      <button 
        onClick={() => {
          if(file){
            const formData = new FormData()
            formData.append("file", file)
            formData.append("oneTimeUse", true)
            formData.append("expiresAt", "2025-08-20T12:00:00Z")
            fetch('http://localhost:8080/api/upload', {
              method: 'POST',
              body: formData
            })
            .then(response => {
              if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
              }
              return response.json();
            })
            .then(data => {
              console.log("Upload successful:", data);
              setUploadLink(data.link)
            })
            .catch(err => {
              console.error("Upload failed:", err);
            });
          }
          else{
            console.log("No File Selected Yet")
          }
        }}
      >
        Upload
      </button>
      {uploadLink && (
        <div>
          <p>Your file has been uploaded!</p>
          <a href={uploadLink} target="_blank" rel="noopener noreferrer">
          {uploadLink}
          </a>
        </div>
      )}
    </div>
  )
}

export default App

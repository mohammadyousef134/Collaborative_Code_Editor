import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";

function Documents() {

  const { projectId } = useParams();
  const navigate = useNavigate();

  const [documents, setDocuments] = useState([]);
  const [newDocumentName, setNewDocumentName] = useState("");

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {

    try {
      const res = await api.get(`/projects/${projectId}/documents`);
      setDocuments(res.data);
    } catch (err) {
      console.error("Failed to load documents", err);
    }

  };

  const createDocument = async () => {

    if (!newDocumentName.trim()) return;

    await api.post(`/projects/${projectId}/documents`, {
      name: newDocumentName
    });

    setNewDocumentName("");
    loadDocuments();

  };

  const deleteDocument = async (id) => {

    await api.delete(`/projects/${projectId}/documents/${id}`);

    loadDocuments();

  };

  return (
    <div>

      <h2>Documents</h2>

      <input
        placeholder="New document name"
        value={newDocumentName}
        onChange={(e) => setNewDocumentName(e.target.value)}
      />

      <button onClick={createDocument}>
        Create Document
      </button>

      <hr />

      {documents.map(doc => (
        <div key={doc.id}>

          <span
            onClick={() => navigate(`/projects/${projectId}/documents/${doc.id}`)}
            style={{ cursor: "pointer", marginRight: "10px" }}
          >
            {doc.name}
          </span>

          <button onClick={() => deleteDocument(doc.id)}>
            Delete
          </button>

        </div>
      ))}

    </div>
  );
}

export default Documents;
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";

function Documents() {

  const { projectId } = useParams();
  const navigate = useNavigate();

  const [documents, setDocuments] = useState([]);
  const [newDocumentName, setNewDocumentName] = useState("");
  const [inviteEmail, setInviteEmail] = useState("");
  const [language, setLanguage] = useState("javascript");

  useEffect(() => {
    loadDocuments();
  }, []);

  const inviteUser = async () => {
    if (!inviteEmail.trim()) return;

    await api.post(`/projects/${projectId}/invite`, {
      email: inviteEmail
    });

    setInviteEmail("");

    alert("Invitation sent");

  };

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
      name: newDocumentName,
      language: language
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

      <h3>Invite collaborator</h3>

      <input
        placeholder="User email"
        value={inviteEmail}
        onChange={(e) => setInviteEmail(e.target.value)}
      />

      <button onClick={inviteUser}>
        Invite
      </button>

      <hr />

      <h3>Documents</h3>

      <input
        placeholder="New document name"
        value={newDocumentName}
        onChange={(e) => setNewDocumentName(e.target.value)}
      />
      <select value={language} onChange={(e) => setLanguage(e.target.value)}>
        <option value="javascript">JavaScript</option>
        <option value="python">Python</option>
        <option value="java">Java</option>
        <option value="cpp">C++</option>
      </select>

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
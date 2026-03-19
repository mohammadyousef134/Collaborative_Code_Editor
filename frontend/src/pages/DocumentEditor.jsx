import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import CodeEditor from "../components/CodeEditor";

function DocumentEditor() {
  const { projectId, id } = useParams();
  const [initialContent, setInitialContent] = useState(null);

  useEffect(() => {
    const load = async () => {
      const res = await api.get(`/projects/${projectId}/documents/${id}`);
      setInitialContent(res.data.content || "");
    };
    load();
  }, [id, projectId]);

  if (initialContent === null) {
    return <div style={{ color: "#fff", padding: 20 }}>Loading...</div>;
  }

  return (
    <CodeEditor
      projectId={projectId}
      documentId={id}
      initialContent={initialContent}
    />
  );
}

export default DocumentEditor;
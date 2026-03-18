import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import CodeEditor from "../components/CodeEditor";

function DocumentEditor() {

  const { projectId, id } = useParams();
  const [initialContent, setInitialContent] = useState(null);

  // Load the last saved content from DB once on mount
  // This is passed to CodeEditor to seed Yjs if the room is empty
  useEffect(() => {
    const load = async () => {
      const res = await api.get(`/projects/${projectId}/documents/${id}`);
      setInitialContent(res.data.content || "");
    };
    load();
  }, []);

  // Don't render until content is fetched
  if (initialContent === null) {
    return <div style={{ color: "#fff", padding: 20 }}>Loading...</div>;
  }

  return (
    <CodeEditor
      documentId={id}
      initialContent={initialContent}
    />
  );
}

export default DocumentEditor;
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Editor from "@monaco-editor/react";
import api from "../api/api";
import CodeEditor from "../components/CodeEditor";

function DocumentEditor() {

    const { id } = useParams();
    const { projectId } = useParams();
    const [code, setCode] = useState("");

    useEffect(() => {
        loadDocument()
    }, []);

    const loadDocument = async () => {
        const res = await api.get(
            `/projects/${projectId}/documents/${id}`
        );
        // console.log("Document response:", res.data);

        setCode(res.data.content || "");
    };

    return (
        <CodeEditor
            code = {code}
            setCode={setCode}
        />
    );
}

export default DocumentEditor;
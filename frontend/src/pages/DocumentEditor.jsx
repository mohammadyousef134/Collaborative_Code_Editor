import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import CodeEditor from "../components/CodeEditor";

function DocumentEditor() {

    const {projectId, id } = useParams();

    const [code, setCode] = useState("");
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        loadDocument();
    }, []);

    useEffect(() => {

        if (!loaded) return;

        const timeout = setTimeout(() => {
            saveDocument();
        }, 1000);

        return () => clearTimeout(timeout);

    }, [code]);

    const saveDocument = async () => {
        await api.put(`/projects/${projectId}/documents/${id}`, {
            content: code
        });

    };

    const loadDocument = async () => {

        const res = await api.get(
            `/projects/${projectId}/documents/${id}`
        );

        setCode(res.data.content || "");
        setLoaded(true);
    };

    return (
        <CodeEditor
            code={code}
            setCode={setCode}
        />
    );
}

export default DocumentEditor;
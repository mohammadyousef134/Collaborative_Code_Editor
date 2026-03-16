import { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import CodeEditor from "../components/CodeEditor";
import { connectWebSocket, sendMessage } from "../websocket/websocket";

function DocumentEditor() {

    const { projectId, id } = useParams();

    const [code, setCode] = useState("");
    const [loaded, setLoaded] = useState(false);
    const [saveStatus, setSaveStatus] = useState("saved");

    const editorRef = useRef(null);
    const clientId = useRef(crypto.randomUUID());
    const isRemoteChange = useRef(false);

    useEffect(() => {
        loadDocument();
    }, []);

    useEffect(() => {

        if (!loaded) return;

        setSaveStatus("unsaved");

        const timeout = setTimeout(() => {
            saveDocument();
        }, 1000);

        return () => clearTimeout(timeout);

    }, [code]);

    const saveDocument = async () => {

        setSaveStatus("saving");

        await api.put(`/projects/${projectId}/documents/${id}`, {
            content: code
        });

        setSaveStatus("saved");

    };

    const loadDocument = async () => {

        const res = await api.get(
            `/projects/${projectId}/documents/${id}`
        );

        setCode(res.data.content || "");
        setLoaded(true);

    };

    const handleEdit = (operation) => {

        sendMessage(
            id,
            JSON.stringify({
                ...operation,
                sender: clientId.current
            })
        );

    };

    useEffect(() => {

        connectWebSocket(id, (message) => {

            const operation = JSON.parse(message);
            if (operation.sender === clientId.current) return;

            const editor = editorRef.current;
            if (!editor) return;

            const model = editor.getModel();
            if (!model) return;

            isRemoteChange.current = true;

            const start = model.getPositionAt(operation.position);
            const end = model.getPositionAt(operation.position + operation.length);

            model.applyEdits([
                {
                    range: {
                        startLineNumber: start.lineNumber,
                        startColumn: start.column,
                        endLineNumber: end.lineNumber,
                        endColumn: end.column,
                    },
                    text: operation.text,
                }
            ]);

            isRemoteChange.current = false;

        });

    }, []);

    const statusColor = {
        saved: "#4caf50",
        saving: "#aaaaaa",
        unsaved: "#aaaaaa",
    };

    const statusText = {
        saved: "Saved ✓",
        saving: "Saving...",
        unsaved: "Unsaved",
    };

    return (
        <div style={{ position: "relative" }}>
            <div style={{
                position: "absolute",
                top: 10,
                right: 16,
                zIndex: 10,
                fontSize: 13,
                color: statusColor[saveStatus],
                fontFamily: "monospace",
                pointerEvents: "none",
            }}>
                {statusText[saveStatus]}
            </div>
            
            {loaded && (
                <CodeEditor
                    code={code}
                    setCode={setCode}
                    onEdit={handleEdit}
                    editorRef={editorRef}
                    isRemoteChange={isRemoteChange}
                />
            )}
        </div>
    );

}

export default DocumentEditor;
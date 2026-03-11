import { useEffect, useRef } from "react";
import Editor from "@monaco-editor/react";
import * as Y from "yjs";
import { MonacoBinding } from "y-monaco";

function CodeEditor({ code, setCode }) {

  const editorRef = useRef(null);
  const ydocRef = useRef(null);
  const yTextRef = useRef(null);

  function handleEditorDidMount(editor) {

    editorRef.current = editor;

    const ydoc = new Y.Doc();
    ydocRef.current = ydoc;

    const yText = ydoc.getText("monaco");
    yTextRef.current = yText;

    new MonacoBinding(
      yText,
      editor.getModel(),
      new Set([editor]),
      null
    );

    yText.observe(() => {
      const updatedText = yText.toString();
      setCode(updatedText);
    });
  }

  // Sync initial document content when it loads
  useEffect(() => {

    if (!yTextRef.current) return;

    const yText = yTextRef.current;

    yText.delete(0, yText.length);
    yText.insert(0, code || "");

  }, [code]);

  return (
    <Editor
      height="90vh"
      defaultLanguage="javascript"
      theme="vs-dark"
      onMount={handleEditorDidMount}
    />
  );
}

export default CodeEditor;
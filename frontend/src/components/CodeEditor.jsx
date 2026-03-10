import { useEffect, useRef } from "react";
import Editor from "@monaco-editor/react";
import * as Y from "yjs";
import { MonacoBinding } from "y-monaco";

function CodeEditor({ code, setCode }) {

  const editorRef = useRef(null);
  const ydocRef = useRef(null);

  function handleEditorDidMount(editor, monaco) {

    editorRef.current = editor;

    // Create Yjs document
    const ydoc = new Y.Doc();
    ydocRef.current = ydoc;

    // Create shared text type
    const yText = ydoc.getText("monaco");

    // Bind Monaco to Yjs
    new MonacoBinding(
      yText,
      editor.getModel(),
      new Set([editor]),
      null
    );

    // Initialize editor content
    yText.insert(0, code || "");
  }

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
import Editor from "@monaco-editor/react";

function CodeEditor({ code, setCode, editorRef, onEdit, isRemoteChange }) {

  function handleEditorDidMount(editor) {

    editorRef.current = editor;

    editor.onDidChangeModelContent((event) => {

      const value = editor.getValue();
      setCode(value);

      if (!isRemoteChange.current) {
        event.changes.forEach((change) => {
          onEdit?.({
            position: change.rangeOffset,
            length: change.rangeLength,
            text: change.text,
          });
        });
      }

    });

  }

  return (
    <Editor
      height="90vh"
      defaultLanguage="javascript"
      theme="vs-dark"
      defaultValue={code}
      onMount={handleEditorDidMount}
    />
  );
}

export default CodeEditor;
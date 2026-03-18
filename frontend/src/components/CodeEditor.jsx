import { useEffect, useRef } from "react";
import Editor from "@monaco-editor/react";
import * as Y from "yjs";
import { WebsocketProvider } from "y-websocket";
import { MonacoBinding } from "y-monaco";

function CodeEditor({ documentId, initialContent }) {

  const ydocRef    = useRef(null);
  const providerRef = useRef(null);
  const bindingRef  = useRef(null);

  function handleEditorDidMount(editor) {

    // 1. Create the shared Yjs document
    const ydoc = new Y.Doc();
    ydocRef.current = ydoc;

    // 2. Get a shared text type — this is the actual content all clients share
    const ytext = ydoc.getText("monaco");

    // 3. Connect to your Yjs server (server.cjs running on port 1234)
    //    All clients with the same documentId join the same room and sync
    const provider = new WebsocketProvider(
      "ws://localhost:1234",
      `document-${documentId}`,
      ydoc
    );
    providerRef.current = provider;

    // 4. Once synced with the server, seed content if the doc is brand new
    provider.on("sync", (isSynced) => {
      if (isSynced && ytext.length === 0 && initialContent) {
        ytext.insert(0, initialContent);
      }
    });

    // 5. Bind Yjs ↔ Monaco — this is the magic line.
    //    Any local edit updates ytext → broadcasts to all clients.
    //    Any remote ytext change updates Monaco automatically.
    //    You no longer need sendMessage, isRemoteChange, or applyEdits.
    const binding = new MonacoBinding(
      ytext,
      editor.getModel(),
      new Set([editor]),
      provider.awareness
    );
    bindingRef.current = binding;

  }

  // Clean up everything when the component unmounts
  useEffect(() => {
    return () => {
      bindingRef.current?.destroy();
      providerRef.current?.destroy();
      ydocRef.current?.destroy();
    };
  }, []);

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
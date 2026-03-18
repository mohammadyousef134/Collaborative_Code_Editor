// import { Client } from "@stomp/stompjs";
// import SockJS from "sockjs-client";

// let stompClient = null;

// export function connectWebSocket(documentId, onMessageReceived) {

//   const socket = new SockJS("http://localhost:8080/ws");

//   stompClient = new Client({
//     webSocketFactory: () => socket,

//     reconnectDelay: 5000,

//     onConnect: () => {

//       console.log("Connected to WebSocket");

//       stompClient.subscribe(`/topic/document/${documentId}`, (message) => {
//         if (onMessageReceived) {
//           onMessageReceived(message.body);
//         }

//       });

//     }
//   });

//   stompClient.activate();
// }

// export function sendMessage(documentId, message) {

//   if (stompClient && stompClient.connected) {
//     stompClient.publish({
//       destination: `/app/document/${documentId}/edit`,
//       body: message
//     });

//   }
// }
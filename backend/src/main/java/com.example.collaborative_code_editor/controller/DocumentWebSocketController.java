//package com.example.collaborative_code_editor.controller;
//
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class DocumentWebSocketController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public DocumentWebSocketController(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @MessageMapping("/document/{documentId}/edit")
//    public void handleEdit(
//            @DestinationVariable String documentId,
//            String message
//    ) {
//
//        messagingTemplate.convertAndSend(
//                "/topic/document/" + documentId,
//                message
//        );
//
//    }
//}
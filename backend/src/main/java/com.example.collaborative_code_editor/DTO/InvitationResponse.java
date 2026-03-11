package com.example.collaborative_code_editor.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class InvitationResponse {
    private Long invitationId;
    private Long projectId;
    private String projectName;
    private String invitedBy;

}
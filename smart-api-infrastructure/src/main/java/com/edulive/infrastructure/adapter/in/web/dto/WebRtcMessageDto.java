package com.edulive.infrastructure.adapter.in.web.dto;

public class WebRtcMessageDto {
    private String type;
    private String roomId;
    private String senderId;
    private String targetId;
    private String sdp;
    private IceCandidateDto candidate;

    public WebRtcMessageDto() {
    }

    public WebRtcMessageDto(String type, String roomId, String senderId, String targetId, String sdp, IceCandidateDto candidate) {
        this.type = type;
        this.roomId = roomId;
        this.senderId = senderId;
        this.targetId = targetId;
        this.sdp = sdp;
        this.candidate = candidate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public IceCandidateDto getCandidate() {
        return candidate;
    }

    public void setCandidate(IceCandidateDto candidate) {
        this.candidate = candidate;
    }
}

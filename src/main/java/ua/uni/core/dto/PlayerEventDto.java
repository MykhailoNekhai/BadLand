package ua.uni.core.dto;

public class PlayerEventDto {
    private String type;
    private long createdAt;
    private String details;

    public PlayerEventDto() {
    }

    public PlayerEventDto(String type, long createdAt, String details) {
        this.type = type;
        this.createdAt = createdAt;
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

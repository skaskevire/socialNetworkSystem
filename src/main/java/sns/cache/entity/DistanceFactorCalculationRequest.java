package sns.cache.entity;

public class DistanceFactorCalculationRequest {
    private String sourceUser;
    private String targetUser;
    private String status;
    private Integer distanceFactor;

    public String getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(String sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDistanceFactor() {
        return distanceFactor;
    }

    public void setDistanceFactor(Integer distanceFactor) {
        this.distanceFactor = distanceFactor;
    }
}

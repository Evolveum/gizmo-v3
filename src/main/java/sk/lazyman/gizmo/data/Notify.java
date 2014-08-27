package sk.lazyman.gizmo.data;

public class Notify {

    private int hashCode = Integer.MIN_VALUE;

    private Integer userId;

    private Integer projectId;

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
        this.hashCode = Integer.MIN_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Notify)) {
            return false;
        } else {
            Notify notify = (Notify) obj;
            if (null != this.getUserId() && null != notify.getUserId()) {
                if (!this.getUserId().equals(notify.getUserId())) {
                    return false;
                }
            } else {
                return false;
            }
            if (null != this.getProjectId() && null != notify.getProjectId()) {
                if (!this.getProjectId().equals(notify.getProjectId())) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            StringBuilder sb = new StringBuilder();
            if (null != this.getUserId()) {
                sb.append(this.getUserId().hashCode());
                sb.append(":");
            } else {
                return super.hashCode();
            }
            if (null != this.getProjectId()) {
                sb.append(this.getProjectId().hashCode());
                sb.append(":");
            } else {
                return super.hashCode();
            }
            this.hashCode = sb.toString().hashCode();
        }
        return this.hashCode;
    }
}
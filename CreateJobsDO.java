public class CreateJobsDO {
    private int id;
    private int workerId;
    private int workDuration;
    private java.sql.Timestamp startTime;
    private java.sql.Timestamp startedTime;
    private java.sql.Timestamp completedTime;
    private java.sql.Timestamp lastStatusTime;
    enum Status {Ready, InProgress, Completed, Error};
    Status status;

    CreateJobsDO(int id, int workerId, int workDuration, java.sql.Timestamp start_time, java.sql.Timestamp started_time,
                 java.sql.Timestamp completed_time, java.sql.Timestamp last_status_time, Status status ) {
        this.id = id;
        this.workerId = workerId;
        this.workDuration = workDuration;
        this.startTime = start_time;
        this.startedTime = started_time;
        this.completedTime = completed_time;
        this.lastStatusTime = last_status_time;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getWorkerId() {
        return workerId;
    }

    public int getWorkDuration() {
        return workDuration;
    }

    public java.sql.Timestamp getStartTime() {
        return startTime;
    }

    public java.sql.Timestamp getStartedTime() {
        return startedTime;
    }

    public java.sql.Timestamp getCompletedTime() {
        return completedTime;
    }

    public java.sql.Timestamp getLastStatusTime() {
        return lastStatusTime;
    }

    public Status getStatus() {
        return status;
    }
}

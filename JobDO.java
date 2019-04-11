public class JobDO {
    private int id;
    private int workDuration;
    private int workerId;
    enum JobStatus {Ready, InProgress, Completed, Error};
    JobStatus jobStatus;

    JobDO(int id, int workDuration, int workerId, JobStatus jobStatus) {
        this.id = id;
        this.workDuration = workDuration;
        this.workerId = workerId;
        this.jobStatus = jobStatus;
    }

    public int getId() {return id;}

    public int getWorkDuration() {return workDuration;}

    public int getWorkerId() { return workerId;}

    public JobStatus getJobStatus() {
        return jobStatus;
    }
}

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Jobs {

    static Random r = new Random();
    static int workerId = r.nextInt((1000 - 100) + 1) + 100;

    static List<CreateJobsDO> getJobs(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select id, worker_id, work_duration, start_time, started_time, " +
                "completed_time, last_status_time, status from jobs order by id");

        List<CreateJobsDO> jobList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            int workerId = rs.getInt("worker_id");
            int workDuration = rs.getInt("work_duration");
            java.sql.Timestamp startTime = rs.getTimestamp("start_time");
            java.sql.Timestamp startedTime = rs.getTimestamp("started_time");
            java.sql.Timestamp completedTime = rs.getTimestamp("completed_time");
            java.sql.Timestamp lastStatusTime = rs.getTimestamp("last_status_time");
            CreateJobsDO.Status status = CreateJobsDO.Status.valueOf(rs.getString("status"));
            CreateJobsDO jobDO = new CreateJobsDO(id, workerId, workDuration, startTime, startedTime, completedTime, lastStatusTime, status);
            jobList.add(jobDO);
        }

        rs.close();
        stmt.close();

        return jobList;
    }

    static void generateJobs(Connection conn) throws SQLException {
        String sql = "insert into jobs (worker_id, work_duration, start_time, started_time, completed_time, status) " +
                        "values (null, ?, ?, null, null, 'Ready')";
        int from = Integer.parseInt(Main.configCj.getFrom());
        int to = Integer.parseInt(Main.configCj.getTo());
        int jobCount = Integer.parseInt(Main.configCj.getJobCount());
        Random r = new Random();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        PreparedStatement pstate = conn.prepareStatement(sql);

        for (int i = 0; i < jobCount; i++) {
            int randomFromTo = r.nextInt((to - from) + 1) + from;
            currentTime = dtf.format(now.plusSeconds(i * Config.WORKER_HEARTBEAT_IVL_SEC));
            pstate.setInt(1, randomFromTo);
            pstate.setString(2, currentTime);
            pstate.executeUpdate();
        }

        conn.commit();
        pstate.close();
    }

    static void processJobs(Connection conn) throws SQLException, InterruptedException {
        while (true) {
            JobDO jobInfo = Jobs.getNextJob(conn);
            if (jobInfo == null) {
                System.out.printf("Worker %d: No job to process\n", workerId);
                Thread.sleep(Config.IDLE_SLEEP_IVL_MSEC);
            } else {
                String problem = process(conn, jobInfo);
                updateStatus(conn, problem, jobInfo.getId(), jobInfo.getWorkerId(), problem == null ? "Completed" : "Error");
            }
        }
    }

    private static void updateStatus(Connection conn, String problem, int id, int worker, String status) throws SQLException {
        String update = "update jobs set status = ?, completed_time = current_time, last_status_time = current_time where id = ? and worker_id = ?;";
        PreparedStatement pstate = conn.prepareStatement(update);
        pstate.setString(1, status);
        pstate.setInt(2, id);
        pstate.setInt(3, workerId);
        pstate.executeUpdate();
        conn.commit();
    }

    private static String process(Connection conn, JobDO jobInfo) throws InterruptedException, SQLException {
        long currentTime = System.currentTimeMillis();
        long finishSleep = currentTime + (jobInfo.getWorkDuration() * 1000);
        System.out.printf("Jobtime takes %d seconds to execute\n", jobInfo.getWorkDuration());

        String update = "update jobs set last_status_time = current_time where id = ? and worker_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(update);
        pstmt.setInt(1, jobInfo.getId());
        pstmt.setInt(2, jobInfo.getWorkerId());

        while (System.currentTimeMillis() <= finishSleep) {
            //currentTime = System.currentTimeMillis();
            if ((finishSleep - currentTime) >= Config.WORKER_HEARTBEAT_IVL_SEC * 1000) {
                Thread.sleep(Config.WORKER_HEARTBEAT_IVL_SEC * 1000);
                int updateCount = pstmt.executeUpdate();
                if (updateCount == 0) {
                    return "Worker " + jobInfo.getWorkerId() + " lost job " + jobInfo.getId();
                }
            } else {
                long diffrence = (finishSleep - currentTime);
                Thread.sleep(Math.max(0,diffrence));
            }
        }

        return null;
    }

    static JobDO getNextJob(Connection conn) throws SQLException, InterruptedException {
        String select =
               "select id, worker_id, work_duration, start_time, started_time, completed_time, last_status_time, status " +
                 "from jobs " +
                "where (started_time is null and status = 'Ready') " +
                   "OR (timestampdiff(second, last_status_time, current_timestamp) > ? and status = 'InProgress') " +
                "order by start_time;";
        String update =
               "update jobs set worker_id = ?, started_time = current_time, last_status_time = current_time, status = 'InProgress' " +
                "where id = ? " +
                  "and (started_time is null OR timestampdiff(second, last_status_time, current_timestamp) > ?);";

        PreparedStatement selectStmt = conn.prepareStatement(select);
        PreparedStatement updateStmt = conn.prepareStatement(update);

        int workerMaxSlackTime = (Config.WORKER_HEARTBEAT_IVL_SEC * Config.WORKER_FREE_PASS_COUNT + 1);
        selectStmt.setInt(1, workerMaxSlackTime);
        ResultSet rs = selectStmt.executeQuery();

        int updateCount = 0;
        JobDO jobInfo = null;

        while (rs.next()) {
            int jobtime = rs.getInt("work_duration");
            int id = rs.getInt("id");
            JobDO.JobStatus jobStatus = JobDO.JobStatus.valueOf(rs.getString("status"));

            updateStmt.setInt(1, workerId);
            updateStmt.setInt(2, id);
            updateStmt.setInt(3, Config.WORKER_HEARTBEAT_IVL_SEC);

            updateCount = updateStmt.executeUpdate();
            conn.commit();
            if (updateCount == 1) {
                jobInfo = new JobDO(id, jobtime, workerId, jobStatus);
                System.out.printf("Job with id = %d was succesfully captured and updated by worker = %d\n", id, workerId);
                break;
            }
        }

        conn.commit();
        rs.close();
        selectStmt.close();
        updateStmt.close();

        return jobInfo;
    }
}

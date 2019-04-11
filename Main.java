import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {

    static Config configCj;

    public static void main(String args[]) throws SQLException, InterruptedException{
        Main h = new Main();
        h.run(args);
    }

    private void run(String[] args) throws SQLException, InterruptedException {
        Main.configCj = new Config(args);

        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" + Main.configCj.getConnStr() + "?useSSL=false",
                Main.configCj.getUserName(), Main.configCj.getPassword()
        );
        conn.setAutoCommit(false);

        List<CreateJobsDO> jobsList= Jobs.getJobs(conn);

        for(CreateJobsDO job: jobsList) {
            System.out.printf("id = %d start_time = %s status = %s\n", job.getId(), job.getStartTime(), job.getStatus());
        }

        if(configCj.getCommand() == Config.Command.Generate) {
            Jobs.generateJobs(conn);
        } else if(configCj.getCommand() == Config.Command.Process){
            Jobs.processJobs(conn);
        } else {
            System.out.printf("Unknown command %s\n", configCj.getCommand());
        }

        conn.close();
    }

}

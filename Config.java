class Config {
    static final int IDLE_SLEEP_IVL_MSEC = 3*1000;
    static final int WORKER_HEARTBEAT_IVL_SEC = 10;
    static final int WORKER_FREE_PASS_COUNT = 3;


    private String connStr;
    private String userName;
    private String password;
    private String jobCount;
    private String from;
    private String to;
    enum Command {Generate, Process};
    private Command command;

    Config(String args[]){
        for(int i = 0; i < args.length; i++ ) {
            if(args[i].equals("--connStr")){
                this.connStr = args[i+1];
            } else if(args[i].equals("-u")){
                this.userName = args[i+1];
            } else if(args[i].equals("-p")){
                this.password = args[i+1];
            } else if(args[i].equals("-jobCount")){
                this.jobCount = args[i+1];
                System.out.printf("jobCount = %s\n", jobCount);
            } else if(args[i].equals("-from")) {
                this.from = args[i+1];
                System.out.printf("from = %s\n", from);
            } else if(args[i].equals("-to")) {
                this.to = args[i+1];
                System.out.printf("to = %s\n", to);
            } else if(args[i].equals("process")) {
                command = Command.Process;
            } else if(args[i].equals("generate")) {
                command = Command.Generate;
            }
        }

        if(command == Command.valueOf("Generate")) {
            if(this.jobCount == null) {
                throw new IllegalArgumentException("Check job Count parameter\n");
            }
            if(this.from == null) {
                throw new IllegalArgumentException("Check from parameter\n");
            }
            if(this.to == null) {
                throw new IllegalArgumentException(("Check to parameter\n"));
            }


        }



        if(this.connStr == null) {
            throw new IllegalArgumentException("Check connection string parameter\n");
        }

        if(this.password == null) {
            throw new IllegalArgumentException("Check password parameter\n");
        }

        if(this.userName == null) {
            throw new IllegalArgumentException("Check username parameter\n");
        }
    }

    String getConnStr(){
        return this.connStr;
    }

    String getUserName() {
        return this.userName;
    }

    String getPassword() {
        return this.password;
    }

    String getJobCount() {
        return jobCount;
    }

    String getFrom() {
        return from;
    }

    String getTo() {
        return to;
    }

    Command getCommand() {
        return command;
    }
}

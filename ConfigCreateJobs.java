class ConfigCreateJobs {
    private String connStr;
    private String userName;
    private String password;

    ConfigCreateJobs(String args[]){
        for(int i = 0; i < args.length; i++ ) {
            if(args[i].equals("--connStr")){
                this.connStr = args[i+1];
            } else if(args[i].equals("-u")){
                this.userName = args[i+1];
            } else if(args[i].equals("-p")){
                this.password = args[i+1];
            }

            i++;
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
}

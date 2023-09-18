package net.kyrptonaught.ToolBox;

public class State {
    public enum STATES {
        MENU,
        EXISTING_INSTALLS_LIST,
        EXISTING_INSTALL,
        INSTALLER,
        EXIT
    }

    STATES state;
    String args;

    public State(STATES state, String args) {
        setState(state);
        this.args = args;
    }


    public void setState(STATES state) {
        this.state = state;
    }

    public STATES getState() {
        return state;
    }

    public String getArgs() {
        return args;
    }

    public static class ServerInfoState extends State {
        InstalledServerInfo serverInfo;

        public ServerInfoState(STATES state, InstalledServerInfo serverInfo, String args) {
            super(state, args);
            this.serverInfo = serverInfo;
        }

        public InstalledServerInfo getServerInfo() {
            return serverInfo;
        }
    }
}

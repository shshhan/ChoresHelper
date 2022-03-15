import com.jcraft.jsch.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TerminalAccess{
    private String USERNAME;
    private String PASSWORD;
    private String HOST;
    private int PORT;

    private Session session;
    private Channel channel;
    private ChannelExec channelExec;


    private void connectSSH()throws JSchException, IOException{
        System.out.println(">>>>>> ConnectSSH invoked.");

        JSch jsch = new JSch();

        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileInputStream("src/main/java/TerminalAccess.properties"));
        this.USERNAME = properties.getProperty("USERNAME");
        this.PASSWORD = properties.getProperty("PASSWORD");
        this.HOST = properties.getProperty("HOST");
        this.PORT = Integer.parseInt(properties.getProperty("PORT"));

        session = jsch.getSession(this.USERNAME, this.HOST, this.PORT);
        session.setPassword(this.PASSWORD);

        session.setConfig(properties);

        session.connect();
    }

    public String getCommandResult(String command)throws JSchException, IOException{
        System.out.println(">>>>>> getCommandResult("+command+") invoked.");

        StringBuilder response = new StringBuilder();

        connectSSH();

        channel = session.openChannel("exec");
        channelExec =(ChannelExec)channel;

        channelExec.setCommand(command);

        InputStream inputStream = channelExec.getInputStream();
        channelExec.connect();

        byte[]buffer = new byte[8192];
        int decodedLength;

        while( (decodedLength = inputStream.read(buffer, 0, buffer.length))> 0){
            response.append(new String(buffer, 0, decodedLength));
        }

        return response.toString();
    }


    public void close(){
        System.out.println(">>>>>> Terminal.close() invoked.");

        channelExec.disconnect();
        channel.disconnect();
        session.disconnect();
    }
}

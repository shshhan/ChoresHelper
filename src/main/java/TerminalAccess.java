import com.jcraft.jsch.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 다른 서버에 접근하기 위한 클래스
 */
public class TerminalAccess{
    private String USERNAME;
    private String PASSWORD;
    private String HOST;
    private int PORT;

    private Session session;
    private Channel channel;
    private ChannelExec channelExec;

    /**
     * TerminalAccess.properties의 내용으로 다른 서버에 접근하기 위한 클래스
     * @throws JSchException
     * @throws IOException
     */
    private void connectSSH()throws JSchException, IOException{
        System.out.println(">>>>>> ConnectSSH invoked.");

        JSch jsch = new JSch();

        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileInputStream("src/main/properties/TerminalAccess.properties"));
        this.USERNAME = properties.getProperty("USERNAME");
        this.PASSWORD = properties.getProperty("PASSWORD");
        this.HOST = properties.getProperty("HOST");
        this.PORT = Integer.parseInt(properties.getProperty("PORT"));

        session = jsch.getSession(this.USERNAME, this.HOST, this.PORT);
        session.setPassword(this.PASSWORD);

        session.setConfig(properties);

        session.connect();
    }

    /**
     * 쉘에 명령어를 입력하면 보여지는 결과값을 리턴하는 메서드
     * @param command 쉘에 입력할 명령어
     * @return 명령어 수행에 대한 결과(String)
     * @throws JSchException
     * @throws IOException
     */
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

    /**
     * 객체를 닫아주기 위한 메서드
     */
    public void close(){
        System.out.println(">>>>>> Terminal.close() invoked.");

        channelExec.disconnect();
        channel.disconnect();
        session.disconnect();
    }
}

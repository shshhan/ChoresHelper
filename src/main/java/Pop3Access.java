import javax.mail.*;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * pop3 서버에 접근하기 위한 클래스
 */
public class Pop3Access{
    private String USERNAME;
    private String PASSWORD;
    private String MAIL_HOST;
    private String PORT;

    private final int number = 50;      // 한번에 가져올 메일 갯수

    private Session emailSession;
    private Store store;
    private Folder inbox;

    Message[]messages;

    /**
     * Pop3Access.properties의 내용으로 pop3 서버에 접근하는 메서드
     * @throws MessagingException
     * @throws IOException
     */
    private void connectPop3()throws MessagingException, IOException{
        System.out.println(">>>>>> connectPop3 invoked.");

        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileInputStream("src/main/properties/Pop3Access.properties"));

        this.USERNAME = properties.getProperty("USERNAME");
        this.PASSWORD = properties.getProperty("PASSWORD");
        this.MAIL_HOST = properties.getProperty("mail.pop3.host");
        this.PORT = properties.getProperty("mail.pop3.port");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }};

        emailSession = Session.getDefaultInstance(properties, auth);

        store = emailSession.getStore("pop3");

        store.connect(MAIL_HOST, USERNAME, PASSWORD);
    }


    /**
     * pop3 서버의 받은 메일함에서 가장 최신 메일부터 선언한 갯수만큼 메일을 가져오는 메서드
     * @throws MessagingException
     * @throws IOException
     */
    private void getMailList()throws MessagingException, IOException{
        System.out.println(">>>>>> getMailList() invoked.");

        connectPop3();

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        messages = inbox.getMessages(inbox.getMessageCount()-this.number+1, inbox.getMessageCount());
    }

    /**
     * 제목에 오늘 날짜가 적힌 메일을 찾아 원하는 형태로 데이터를 가공하는 메서드
     * @param formattedNow 오늘날짜(yyyy/MM/dd)
     * @return  원하는 형태로 가공된 문자열(String)
     * @throws MessagingException
     * @throws IOException
     */
    public String todaysMailList(String formattedNow)throws MessagingException, IOException{

        if(messages == null) {
            this.getMailList();
        }

        StringBuilder sb = new StringBuilder();

        int n = 0;
        int oneMoreLine = 0;

        for(int i = messages.length-1 ; i >= 0; i--){
            Message m = messages[i];

            if(!m.getSubject().contains(formattedNow)){
                continue;
            }

            if(oneMoreLine == 0
                    && m.getHeader("Date")[0].split(" ")[4].split(":")[0].equals("10")){
                sb.append("\n\n");
                oneMoreLine = 1;
            }

            sb.append(m.getFrom()[0].toString().split(" ")[0]);
            sb.append(m.getSubject());
            sb.append(m.getHeader("Date")[0].split(" ")[4]);
            sb.append(" \n");
            n++;
        }

        System.out.println("총 메일 수 : " + n);

        String mailList = sb.toString();

        return mailList;
    }


    /**
     * 두 파라미터 조건에 맞는 메일을 찾아 그 내용을 return 하는 메서드
     * @param formattedYesterday    어제날짜(yyyy/MM/dd)
     * @param condition 제목에 포함되어 있어야 하는 문자열(String)
     * @return 메일의 내용(String)
     * @throws MessagingException
     * @throws IOException
     */
    public String getMailContent(String formattedYesterday, String condition)throws MessagingException, IOException{
        System.out.println(">>>>>> getMailContent("+formattedYesterday+") invoked.");

        if(messages == null) {
            this.getMailList();
        }

        StringBuilder sb = new StringBuilder();

        for(int i = messages.length-1 ; i >= 0; i--){
            Message m = messages[i];

            if(m.getSubject().contains(condition)
                    && m.getFrom()[0].toString().split(" ")[0].contains(formattedYesterday)){

                String[]ary = m.getContent().toString().split("\n");

                for(int j = 0; j<ary.length; j+=2){
                    sb.append(ary[j]);
                }

                break;

            }

        }

        return sb.toString();
    }


    /**
     * 객체를 닫아주기 위한 메서드
     */
    public void close() {
        System.out.println(">>>>>> Pop3.close() invoked.");

        try{
            inbox.close(false);
            store.close();
        }catch(MessagingException e) {
            e.printStackTrace();
        }

    }

}

import javax.mail.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Pop3Access{
    private String USERNAME;
    private String PASSWORD;
    private String MAIL_HOST;
    private String PORT;

    private Session emailSession;
    private Store store;
    private Folder inbox;

    Message[]messages;

    private void connectPop3()throws MessagingException, IOException{
        System.out.println(">>>>>> connectPop3 invoked.");

        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileInputStream("src/main/java/Pop3Access.properties"));

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

    private void getMailList(int number)throws MessagingException, IOException{
        System.out.println(">>>>>> getMailList(" + number + ") invoked.");

        connectPop3();

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        messages = inbox.getMessages(inbox.getMessageCount()-number+1, inbox.getMessageCount());
        System.out.println(messages);
    }

    public String todaysMailList(String formattedNow, int number)throws MessagingException, IOException{
        System.out.println(">>>>>> todaysMailList(" + formattedNow + ") invoked.");

        if(messages == null) {
            this.getMailList(number);
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


    public String getMailContent(String formattedYesterday, String condition)throws MessagingException, IOException{
        System.out.println(">>>>>> getMailContent("+formattedYesterday+") invoked.");

        if(messages == null) {
            this.getMailList(50);
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

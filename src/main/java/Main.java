import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.time.StopWatch;

import javax.mail.MessagingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main{
    public static void main(String[]args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        /**
         * conTxt : 0 = Console out, 1 = .txt out
         */
        final int conTxt = 1;
        final String filePath = "/Users/shawn/Desktop/";
        final String commandPrefix = "find / -name \"*";
        final String commandPostfix = "*";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedNow = now.format(dateTimeFormatter);
        String formattedYesterday = now.minusDays(1).format(dateTimeFormatter);

        TerminalAccess terminal = new TerminalAccess();
        Pop3Access pop3 = new Pop3Access();

        String commandResult = null;
        String mailList = null;
        String content = null;

        try{
            commandResult = terminal.getCommandResult(commandPrefix+formattedNow+commandPostfix);
            mailList = pop3.todaysMailList(formattedNow);
            content = pop3.getMailContent(formattedYesterday, "메일 제목의 조건");

            if(conTxt == 0) {
                System.out.println("========== commandResult ==========");
                System.out.println(commandResult);
                System.out.println("========== mailList ==========");
                System.out.println(mailList);
                System.out.println("========== content ==========");
                System.out.println(content);

            }else{
                String time = "";
                if(now.getHour()< 11) {
                    time = " 오전 ";
                }else{
                    time = " 오후 ";
                }

                String fileName = filePath + formattedNow + time + ".txt";

                FileWriter fw = new FileWriter(fileName, false);
                BufferedWriter bfw = new BufferedWriter(fw);

                bfw.write("========== commandResult ==========\n\n");
                bfw.write(commandResult + "\n\n");
                bfw.write("========== mailList ==========\n\n");
                bfw.write(mailList + "\n\n");
                bfw.write("========== content ==========\n\n");
                bfw.write(content);

                fw.flush();
                bfw.flush();
                fw.close();
                bfw.close();

            }

        }catch(JSchException e) {
            e.printStackTrace();
        }catch(MessagingException | IOException e) {
            e.printStackTrace();
        }finally{
            terminal.close();
            pop3.close();
        }

        stopWatch.stop();
        System.out.println("수행시간: " + stopWatch.getTime()+ " ms");
    }
}

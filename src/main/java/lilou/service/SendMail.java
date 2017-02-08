package lilou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Created by guochunxing on 17-2-7.
 */

@Component
public class SendMail {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${toMailAddress}")
    private String toMailAdress;

    private static final String cmdPath = "/home/guochunxing/下载/wkhtmltox/bin/wkhtmltopdf";
    static final String srcPath = "https://www.baidu.com";
    static final String destPath ="/home/guochunxing/下载/wkhtmltox/bin/baidu.pdf";

    @Scheduled(cron = "0 0 9 * * ? ")//9点执行
    public void sendTool() throws Exception{

        //========执行生成pdf命令
        //命令
        StringBuilder cmd = new StringBuilder();
        cmd.append(cmdPath);
        cmd.append(" ");
        cmd.append("--orientation Landscape");// 参数 --横向
        cmd.append(" ");
        cmd.append("--javascript-delay 5000");//延迟5s
        cmd.append(" ");
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);

        Runtime.getRuntime().exec(cmd.toString()).waitFor();

        String[] mailAddress = toMailAdress.split(",");

        //=======读取pdf发送邮件
        for (String address : mailAddress) {

            this.sendMail(address);
        }
    }

    public void sendMail(String address) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("liloutvkou@126.com");
        helper.setTo(address);
        helper.setSubject("统计");
        helper.setText("请查看附件");
        FileSystemResource file = new FileSystemResource(new File(destPath));
        helper.addAttachment("tvkou.pdf", file);
        mailSender.send(mimeMessage);
    }
}

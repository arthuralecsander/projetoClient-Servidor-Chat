package client;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
public class SendingFileThread implements Runnable {
    
    protected Socket socket;
    private DataOutputStream dos;
    protected SendFile form;
    protected String file;
    protected String receiver;
    protected String sender;
    protected DecimalFormat df = new DecimalFormat("##,#00");
    private final int BUFFER_SIZE = 100;
    
    public SendingFileThread(Socket soc, String file, String receiver, String sender, SendFile frm){
        this.socket = soc;
        this.file = file;
        this.receiver = receiver;
        this.sender = sender;
        this.form = frm;
    }

    @Override
    public void run() {
        try {
            form.disableGUI(true);
            System.out.println("Enviando Arquivo..!");
            dos = new DataOutputStream(socket.getOutputStream());
            /** Write filename, recipient, username  **/
            //  Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Consignee]
            File filename = new File(file);
            int len = (int) filename.length();
            int filesize = (int)Math.ceil(len / BUFFER_SIZE); // get the file size
            String clean_filename = filename.getName();
            dos.writeUTF("CMD_SENDFILE "+ clean_filename.replace(" ", "_") +" "+ filesize +" "+ receiver +" "+ sender);
            System.out.println("de: "+ sender);
            System.out.println("para: "+ receiver);
            /** Create an stream **/
            InputStream input = new FileInputStream(filename);
            OutputStream output = socket.getOutputStream();
            
            
            BufferedInputStream bis = new BufferedInputStream(input);
            /** Create a temporary file storage **/
            byte[] buffer = new byte[BUFFER_SIZE];
            int count, percent = 0;
            while((count = bis.read(buffer)) > 0){
                percent = percent + count;
                int p = (percent / filesize);
                //form.setMyTitle(p +"% Sending File...");
                form.updateProgress(p);
                output.write(buffer, 0, count);
            }
            /* Update AttachmentForm GUI */
            form.setMyTitle("Arquivo foi enviado.!");
            form.updateAttachment(false); //  Update Attachment 
            JOptionPane.showMessageDialog(form, "Arquivo enviado com sucesso.!", "Sucess", JOptionPane.INFORMATION_MESSAGE);
            form.closeThis();
            /* Close Streams */
            output.flush();
            output.close();
            System.out.println("O arquivo foi enviado..!");
        } catch (IOException e) {
            form.updateAttachment(false); //  Update Attachment
            System.out.println("[SendFile]: "+ e.getMessage());
        }
    }
}

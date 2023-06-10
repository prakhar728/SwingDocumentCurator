import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;


public class HTMLWriter {


    public static void writeToHTML(String htmlFileName, Collection<UrlInfo> summaries) {
        BufferedWriter htmlWriter = null;
        try{
            File htmlFile = new File(htmlFileName);
            if (htmlFile.exists()) {
                htmlFile.delete();
            }
            htmlFile.createNewFile();
            htmlWriter = new BufferedWriter(new FileWriter(htmlFile));

            htmlWriter.write("<html><body>Hi, <br/> Here are the some interesting snippets <br/><ul/>");
            for (UrlInfo urlInfo : summaries) {
                String body = "<li><b><a href=\"" + urlInfo.getUrl() + "\" target=\"blank\" a>"
                        + urlInfo.getHeadline() + "</a></b>"
                        + urlInfo.getSummary() + "</li>";
                htmlWriter.write("<br/>" + body);
            }
            htmlWriter.write("</ul><br/></body></html>");


        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                htmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
//https://stackoverflow.com/questions/22310562/java-pull-table-data-from-site

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReadJsoup {
    private final String split = ",";
    String fileName = "C:\\Users\\Admin\\Documents\\Intellij\\IACPM.csv";
    FileWriter fw;

    public ReadJsoup() throws IOException {
        this.fileName = fileName;
    }

    public void readWhole() throws IOException {
        Path pathToFile = Paths.get(fileName);
        File csvFile = new File(fileName);
        if (csvFile.isFile()) {
            csvFile.delete();
        }
        fw = new FileWriter(fileName, true);
        fw.append("Last Name, First Name, Firm Name,Country, Email\n");

        int n = 152;//152;
        String html0 = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_cntrycode=&s_filter1=&s_filter2=";
        readOnePage(html0);
        for (int i = 1; i < n; i++) {//1
            String html = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_filter1=&s_filter2=&page=" + (i + 1);
            readOnePage(html);
            System.out.print("$$$$ Page " + (i + 1) + " finished!");
        }
        fw.flush();
        fw.close();
    }

    public void readOnePage(String html) throws IOException {
        Connection connection = Jsoup.connect(html);
        connection.timeout(500000);
        Document doc = connection.get();
        Elements tableElements = doc.select("table");

        Element firstTable = tableElements.get(0);

        List<Node> firstTableRows = firstTable.childNodes().get(1).childNodes();
        int numRows = firstTableRows.size();
        // skip first row as header
        for (int i = 2; i < numRows; i += 2) {
            Node row = firstTableRows.get(i);
            StringBuilder sb = new StringBuilder();
            if (row instanceof Element) {
                if (row.childNodes().isEmpty()) continue;
                //last name
                sb.append(((Element) row.childNode(1).childNode(0)).text());
                sb.append(split);
                //first name
                if (!row.childNode(3).childNodes().isEmpty())
                    sb.append(((TextNode) row.childNode(3).childNode(0)).text());
                else continue;
                sb.append(split);
                //firm name
                if (!row.childNode(5).childNodes().isEmpty())
                    sb.append(((TextNode) row.childNode(5).childNode(0)).text());
                sb.append(split);
                //country
                if (!row.childNode(7).childNodes().isEmpty())
                    sb.append(((TextNode) row.childNode(7).childNode(0)).text());
                sb.append(split);
                //email
                if (row.childNode(9).childNodes().isEmpty()) continue;
                Node node = row.childNode(9).childNode(0);
                String mailto = null;
                if (node instanceof TextNode)
                    mailto = ((TextNode) row.childNode(9).childNode(0)).attr("href");
                else if (node instanceof Element)
                    mailto = ((Element) row.childNode(9).childNode(0)).attr("href");
                else mailto = "***** need attention *****";
                String email = mailto.substring(mailto.indexOf(":") + 1);
                sb.append(email);
            }
            fw.append(sb.toString()).append("\n");
            System.out.println(sb.toString());
        }

    }

    public static void main(String[] args) throws IOException {
        ReadJsoup readJsoup = new ReadJsoup();
        readJsoup.readWhole();
    }
}
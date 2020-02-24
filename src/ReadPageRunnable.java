import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class ReadPageRunnable implements Runnable {
    private final int idx;
    private final String html;
    private final String[] pageInfos;
    private final String split = ",";

    public ReadPageRunnable(String html, String[] pageInfos, int idx) {
        this.html = html;
        this.pageInfos = pageInfos;
        this.idx = idx;
    }

    //return record for one page
    public String readOnePage(String html) throws IOException {
        Connection connection = Jsoup.connect(html);
        connection.timeout(500000);
        Document doc = connection.get();
        Elements tableElements = doc.select("table");

        Element firstTable = tableElements.get(0);

        List<Node> firstTableRows = firstTable.childNodes().get(1).childNodes();
        int numRows = firstTableRows.size();
        StringBuilder sbt = new StringBuilder();

        for (int i = 2; i < numRows; i += 2) {
            Node row = firstTableRows.get(i);
            StringBuilder sb = new StringBuilder();
            if (row instanceof Element) {
                if (row.childNodes().isEmpty()) continue;
                //last name
                if (!row.childNode(1).childNodes().isEmpty()) {
                    sb.append("\"");
                    sb.append(((Element) row.childNode(1).childNode(0)).text());
                    sb.append("\"");
                } else continue;
                sb.append(split);
                //first name
                if (!row.childNode(3).childNodes().isEmpty())
                    sb.append(((TextNode) row.childNode(3).childNode(0)).text());
                else continue;
                sb.append(split);
                //firm name
                if (!row.childNode(5).childNodes().isEmpty())
                    sb.append(((TextNode) row.childNode(5).childNode(0)).text().replace(",", " "));
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
            sbt.append(sb.toString());
        }
        return sbt.toString();
    }

    @Override
    public void run() {
        try {
            pageInfos[idx] = readOnePage(html);
            System.out.println("-------------Page " + (idx + 1) + " finished!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

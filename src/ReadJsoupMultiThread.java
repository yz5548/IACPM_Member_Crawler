//https://stackoverflow.com/questions/22310562/java-pull-table-data-from-site

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadJsoupMultiThread {
    private final String split = ",";
    String fileName = "C:\\Users\\Admin\\Documents\\Intellij\\IACPM_Parallel.csv";
    FileWriter fw;

    public ReadJsoupMultiThread() throws IOException {
        this.fileName = fileName;
    }

    public void readWhole() throws IOException, InterruptedException {
        Path pathToFile = Paths.get(fileName);
        File csvFile = new File(fileName);
        if (csvFile.isFile()) csvFile.delete();
        fw = new FileWriter(fileName, true);
        fw.append("Last Name, First Name, Firm Name,Country, Email\n");

        int n = 10;//152;
        Thread[] threads = new Thread[n];
        String[] pageInfos = new String[n];
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
        for (int i = 0; i < n; i++) {
            String html = null;
            if (i == 0)
                html = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_cntrycode=&s_filter1=&s_filter2=";
            else
                html = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_filter1=&s_filter2=&page=" + (i + 1);
            executor.execute(new ReadPageRunnable(html, pageInfos, i));
        }
        executor.shutdown();
//        for (int i = 0; i < n; i++) {
//            String html = null;
//            if (i == 0)
//                html = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_cntrycode=&s_filter1=&s_filter2=";
//            else
//                html = "http://web.iacpm.org/member_directory/individual/search-results.dot?searchField=s_lname&searchFieldKey=%25&s_filter1=&s_filter2=&page=" + (i + 1);
//            threads[i] = new Thread(new ReadPageRunnable(html, pageInfos, i));
//        }
//        for (int i = 0; i < n; i++) threads[i].start();
//        for (int i = 0; i < n; i++) threads[i].join();
        for (int i = 0; i < n; i++) {
            fw.append(pageInfos[i]);
        }
        fw.flush();
        fw.close();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        ReadJsoupMultiThread readJsoup = new ReadJsoupMultiThread();
        readJsoup.readWhole();
    }

}
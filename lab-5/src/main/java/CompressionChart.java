import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressionChart {

    public static List<Integer> lzwCompress(String uncompressed) {
        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) dictionary.put("" + (char) i, i);

        String w = "";
        List<Integer> result = new ArrayList<>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) w = wc;
            else {
                result.add(dictionary.get(w));
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }
        if (!w.isEmpty()) result.add(dictionary.get(w));
        return result;
    }

    public static byte[] zipCompress(String input, String entryName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            zos.write(input.getBytes());
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        String[] messages = {
                "TOBEORNOTTOBEORTOBEORNOT",
                "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG",
                "My name is Oleksandr and Oleksandr is my name."
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < messages.length; i++) {
            String msg = messages[i];
            int originalSize = msg.getBytes().length;

            List<Integer> lzw = lzwCompress(msg);
            double lzwRatio = (double) (lzw.size() * Integer.BYTES) / originalSize;

            byte[] zip = zipCompress(msg, "message.txt");
            double zipRatio = (double) zip.length / originalSize;

            String label = "Message " + (i + 1);
            dataset.addValue(lzwRatio, "LZW", label);
            dataset.addValue(zipRatio, "ZIP", label);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Порівняння коефіцієнтів стиснення",
                "Повідомлення",
                "Коефіцієнт стиснення",
                dataset
        );

        File file = new File("compression_chart.png");
        ChartUtils.saveChartAsPNG(file, chart, 800, 600);
        System.out.println("Графік збережено у файлі: " + file.getAbsolutePath());
    }
}

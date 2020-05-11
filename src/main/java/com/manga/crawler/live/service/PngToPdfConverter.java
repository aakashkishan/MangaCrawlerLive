package com.manga.crawler.live.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.manga.crawler.live.utils.DirectoryFilePathUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PngToPdfConverter {

    private final int margin = 5;

    public void convertPngToPdf(String chapterPath, String chapterNumber, String mangaName) throws IOException, DocumentException {

        // Get the PNG Files in the Directory
        Stream<Path> walk = Files.walk(Paths.get(chapterPath));
        List<String> pngFilesInDirectory = walk.filter(Files::isRegularFile)
                .map(x -> x.toString()).collect(Collectors.toList());

        // Sort the PNG Files in the order to be read
        Collections.sort(pngFilesInDirectory, new SortFileOrderComparator());

        // PDF Document initialization
        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, new FileOutputStream(DirectoryFilePathUtils.generatePDFPathForMangaChapterInMangaSeries(chapterNumber, mangaName)));

        // Copy the PNG Images to the PDF File
        pdfDoc.open();
        for(String pngFileInDirectory: pngFilesInDirectory) {
            System.out.println(pngFileInDirectory);
            Image image = Image.getInstance(pngFileInDirectory);
            image.scaleToFit(image.getScaledWidth(), image.getScaledHeight());
            pdfDoc.setMargins(margin, margin, margin, margin);
            pdfDoc.setPageSize(new Rectangle(image.getScaledWidth() + 2 * margin, image.getScaledHeight() + 2 * margin));
            pdfDoc.newPage();
            pdfDoc.add(image);
        }
        pdfDoc.close();

    }

}

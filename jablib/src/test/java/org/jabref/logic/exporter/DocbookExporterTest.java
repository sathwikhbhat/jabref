package org.jabref.logic.exporter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.jabref.logic.util.StandardFileType;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.metadata.SaveOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Answers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DocbookExporterTest {

    public BibDatabaseContext databaseContext = new BibDatabaseContext();
    public Charset charset = StandardCharsets.UTF_8;

    private Exporter exportFormat;

    @BeforeEach
    void setUp() {
        exportFormat = new TemplateExporter(
                "DocBook 4",
                "docbook4",
                "docbook4",
                null,
                StandardFileType.XML,
                mock(LayoutFormatterPreferences.class, Answers.RETURNS_DEEP_STUBS),
                SaveOrder.getDefaultSaveOrder());
    }

    @Test
    void corruptedTitleBraces(@TempDir Path testFolder) throws IOException, SaveException, ParserConfigurationException, TransformerException {
        Path tmpFile = testFolder.resolve("testBraces");

        BibEntry entry = new BibEntry();
        entry.setField(StandardField.TITLE, "Peptidomics of the larval {{{D}rosophila melanogaster}} central nervous system.");

        List<BibEntry> entries = List.of(entry);

        exportFormat.export(databaseContext, tmpFile, entries);

        List<String> lines = Files.readAllLines(tmpFile);
        assertEquals(20, lines.size());
        assertEquals("   <citetitle pubwork=\"article\">Peptidomics of the larval Drosophila melanogaster central nervous system.</citetitle>", lines.get(9));
    }

    @Test
    void corruptedTitleUnicode(@TempDir Path testFolder) throws IOException, SaveException, ParserConfigurationException, TransformerException {
        Path tmpFile = testFolder.resolve("testBraces");

        BibEntry entry = new BibEntry();
        entry.setField(StandardField.TITLE, "Insect neuropeptide bursicon homodimers induce innate immune and stress genes during molting by activating the {NF}-$\\kappa$B transcription factor Relish.");

        List<BibEntry> entries = List.of(entry);

        exportFormat.export(databaseContext, tmpFile, entries);

        List<String> lines = Files.readAllLines(tmpFile);
        assertEquals(20, lines.size());
        assertEquals("   <citetitle pubwork=\"article\">Insect neuropeptide bursicon homodimers induce innate immune and stress genes during molting by activating the NF&#45;&#954;B transcription factor Relish.</citetitle>", lines.get(9));
    }
}

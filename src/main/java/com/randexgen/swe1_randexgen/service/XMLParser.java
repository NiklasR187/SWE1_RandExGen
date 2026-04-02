package com.randexgen.swe1_randexgen.service;

import com.randexgen.swe1_randexgen.datamodel.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses an XML file and converts its content into the internal exam data model.
 *
 * This class reads the XML structure of an exam, including chapters, subtasks,
 * and variants, and creates the corresponding Java objects.
 */
public class XMLParser {

    /**
     * Parses the given XML input stream and creates a complete {@link Exam} object.
     *
     * The method reads the root exam element, extracts its basic data,
     * and iterates over all chapter elements to build the full object structure.
     *
     * @param inputStream the input stream containing the XML exam data
     * @return the parsed exam object
     * @throws Exception if the XML cannot be read or parsed correctly
     */
    public Exam parse(InputStream inputStream) throws Exception {

        // Create the XML parser infrastructure for reading the input stream
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the XML document and normalize its structure
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        // Access the root exam element and create the target exam object
        Element examElement = document.getDocumentElement();
        Exam exam = new Exam();

        // Read the basic exam attributes from the root element
        exam.setId(examElement.getAttribute("id"));
        exam.setTitle(getText(examElement, "title"));

        // Collect all chapter objects defined in the XML document
        List<Chapter> chapters = new ArrayList<>();
        NodeList chapterNodes = document.getElementsByTagName("chapter");

        // Parse each chapter element and add it to the exam
        for (int i = 0; i < chapterNodes.getLength(); i++) {
            Element chapterElement = (Element) chapterNodes.item(i);
            Chapter chapter = parseChapter(chapterElement);
            chapters.add(chapter);
        }

        // Assign the parsed chapters to the exam object
        exam.setChapters(chapters);

        return exam;
    }

    /**
     * Parses a single chapter element and converts it into a {@link Chapter} object.
     *
     * The method reads the chapter metadata, optional regular score information,
     * and all contained subtasks including their variants.
     *
     * @param element the XML element representing a chapter
     * @return the parsed chapter object
     */
    private Chapter parseChapter(Element element) {

        // Create a new chapter object and fill in its basic properties
        Chapter chapter = new Chapter();

        chapter.setId(element.getAttribute("id"));
        chapter.setTitle(getText(element, "title"));

        // Read the exam appearance value and convert it to the matching enum
        chapter.setExamAppearance(
                ExamAppearance.valueOf(getText(element, "examAppearance"))
        );

        // Read the optional selected regular score if it exists in the XML
        String regScoreText = getText(element, "selectedRegularScore");
        if (!regScoreText.isEmpty()) {
            chapter.setSelectedRegularScore(Double.parseDouble(regScoreText));
        }

        // Get all subtask elements belonging to this chapter
        NodeList subtaskNodes = element.getElementsByTagName("subtask");
        List<Subtask> subtasks = new ArrayList<>();

        // Parse each subtask and add it to the chapter
        for (int i = 0; i < subtaskNodes.getLength(); i++) {
            Element subtaskElement = (Element) subtaskNodes.item(i);
            Subtask subtask = parseSubtask(subtaskElement);
            subtasks.add(subtask);
        }

        // Store all parsed subtasks inside the chapter object
        chapter.setSubtasks(subtasks);

        return chapter;
    }

    /**
     * Parses a single subtask element and converts it into a {@link Subtask} object.
     *
     * The method reads the subtask properties such as score, difficulty level,
     * exam type, and all available variants.
     *
     * @param element the XML element representing a subtask
     * @return the parsed subtask object
     */
    private Subtask parseSubtask(Element element) {

        // Create a new subtask object and assign its basic values
        Subtask subtask = new Subtask();

        subtask.setId(element.getAttribute("id"));
        subtask.setTitle(getText(element, "title"));

        // Parse the numerical score and the enum-based properties
        subtask.setScore(
                Double.parseDouble(getText(element, "score"))
        );

        subtask.setDifficultyLevel(
                DifficultyLevel.valueOf(getText(element, "difficultyLevel"))
        );

        subtask.setExamType(
                ExamType.valueOf(getText(element, "examType"))
        );

        // Read all variants that belong to this subtask
        NodeList variantNodes = element.getElementsByTagName("variant");
        List<Variant> variants = new ArrayList<>();

        // Parse every variant element and build the corresponding objects
        for (int i = 0; i < variantNodes.getLength(); i++) {

            Element variantElement = (Element) variantNodes.item(i);
            Variant variant = new Variant();

            variant.setId(variantElement.getAttribute("id"));
            variant.setTaskText(getText(variantElement, "taskText"));
            variant.setAnswerText(getText(variantElement, "answerText"));
            variant.setSolutionText(getText(variantElement, "solutionText"));

            variants.add(variant);
        }

        // Assign the parsed variants to the subtask
        subtask.setVariants(variants);

        return subtask;
    }

    /**
     * Returns the text content of the first XML element with the given tag name.
     *
     * If the tag does not exist inside the given parent element,
     * an empty string is returned instead.
     *
     * @param parent the parent element in which the tag is searched
     * @param tag the name of the child tag
     * @return the text content of the tag, or an empty string if it is missing
     */
    private String getText(Element parent, String tag) {

        // Search for all matching child elements with the given tag name
        NodeList nodes = parent.getElementsByTagName(tag);

        // Return an empty string if the requested tag does not exist
        if (nodes.getLength() == 0) return "";

        // Return the text content of the first matching element
        return nodes.item(0).getTextContent();
    }
}
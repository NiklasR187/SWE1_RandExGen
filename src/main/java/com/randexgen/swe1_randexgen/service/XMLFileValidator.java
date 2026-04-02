package com.randexgen.swe1_randexgen.service;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Validates the structure and content of an XML exam file before it is parsed.
 *
 * This class checks whether all required elements are present in the correct
 * hierarchy and whether numeric values such as scores contain valid data.
 */
public class XMLFileValidator {

    /**
     * Validates the given XML file against the expected exam structure.
     *
     * The method checks the root element, required child elements, and all nested
     * chapter, subtask, and variant elements to ensure the file is structurally correct.
     *
     * @param xmlFile the XML file to validate
     * @throws Exception if the file structure is invalid or required elements are missing
     */
    public static void validate(File xmlFile) throws Exception {

        // Build a DOM document from the given XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        // Access the root element and verify that it is an exam element
        Element root = doc.getDocumentElement();

        if (!root.getNodeName().equals("exam")) {
            throw new Exception("Root element must be <exam>");
        }

        // Check that the root exam element contains the required direct children
        requireDirectChild(root, "title");
        Element chapters = requireDirectChild(root, "chapters");

        // Iterate over all child nodes inside the chapters container
        NodeList chapterNodes = chapters.getChildNodes();

        for (int i = 0; i < chapterNodes.getLength(); i++) {
            Node node = chapterNodes.item(i);

            // Only element nodes are relevant for structural validation
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element chapter = (Element) node;

                // Ensure that only chapter elements are placed inside chapters
                if (!chapter.getTagName().equals("chapter")) {
                    throw new Exception("Invalid element inside <chapters>");
                }

                // Validate the full structure of the current chapter
                validateChapter(chapter);
            }
        }
    }

    /**
     * Validates a single chapter element and all of its nested subtasks.
     *
     * The method checks whether the required chapter fields exist and whether
     * the selected regular score and contained subtasks are valid.
     *
     * @param chapter the chapter element to validate
     * @throws Exception if the chapter structure or content is invalid
     */
    private static void validateChapter(Element chapter) throws Exception {

        // Ensure that all mandatory chapter elements are present
        requireDirectChild(chapter, "title");
        requireDirectChild(chapter, "examAppearance");

        // Validate the selected regular score as a non-negative integer
        Element selectedRegularScoreElement = requireDirectChild(chapter, "selectedRegularScore");
        validateScore(selectedRegularScoreElement.getTextContent(), "Chapter selectedRegularScore");

        // Access the subtasks container of the current chapter
        Element subtasks = requireDirectChild(chapter, "subtasks");
        NodeList subtaskNodes = subtasks.getChildNodes();

        // Validate all subtask elements inside the subtasks container
        for (int i = 0; i < subtaskNodes.getLength(); i++) {
            Node node = subtaskNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element subtask = (Element) node;

                // Only subtask elements are allowed inside subtasks
                if (!subtask.getTagName().equals("subtask")) {
                    throw new Exception("Invalid element inside <subtasks>");
                }

                // Validate the full structure of the current subtask
                validateSubtask(subtask);
            }
        }
    }

    /**
     * Validates a single subtask element and all of its variants.
     *
     * The method checks whether all required subtask fields exist and whether
     * the score and nested variant structure are valid.
     *
     * @param subtask the subtask element to validate
     * @throws Exception if the subtask structure or content is invalid
     */
    private static void validateSubtask(Element subtask) throws Exception {

        // Check all required basic fields of the subtask
        requireDirectChild(subtask, "title");

        // Validate that the score exists and contains a valid numeric value
        Element scoreElement = requireDirectChild(subtask, "score");
        validateScore(scoreElement.getTextContent(), "Subtask");

        // Ensure that the enum-related fields are present
        requireDirectChild(subtask, "difficultyLevel");
        requireDirectChild(subtask, "examType");

        // Access the variants container and validate each contained variant
        Element variants = requireDirectChild(subtask, "variants");
        NodeList variantNodes = variants.getChildNodes();

        for (int i = 0; i < variantNodes.getLength(); i++) {
            Node node = variantNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element variant = (Element) node;

                // Only variant elements are allowed inside variants
                if (!variant.getTagName().equals("variant")) {
                    throw new Exception("Invalid element inside <variants>");
                }

                // Validate the required fields of the current variant
                validateVariant(variant);
            }
        }
    }

    /**
     * Validates a single variant element.
     *
     * The method ensures that each variant contains the required text fields
     * for task, answer, and solution content.
     *
     * @param variant the variant element to validate
     * @throws Exception if a required field is missing
     */
    private static void validateVariant(Element variant) throws Exception {
        // Ensure that all mandatory variant text elements are present
        requireDirectChild(variant, "taskText");
        requireDirectChild(variant, "answerText");
        requireDirectChild(variant, "solutionText");
    }

    /**
     * Validates whether the given score text represents a non-negative number
     * that is either a whole number or a half-step value (e.g. 1.0, 1.5, 2.0).
     *
     * The method trims the input, parses it as a double, and checks that the value
     * is numeric, not negative, and only uses increments of 0.5.
     *
     * @param scoreText the text to validate as a score
     * @param context the context description used in error messages
     * @throws Exception if the score is not numeric, negative, or not a valid 0.5-step value
     */
    private static void validateScore(String scoreText, String context) throws Exception {
        try {
            double score = Double.parseDouble(scoreText.trim());

            if (score < 0) {
                throw new Exception(context + " must be zero or positive");
            }

            double doubledScore = score * 2;

            if (Math.abs(doubledScore - Math.round(doubledScore)) > 0.000001) {
                throw new Exception(context + " must be a whole or half number");
            }

        } catch (NumberFormatException e) {
            throw new Exception(context + " is not a number");
        }
    }

    /**
     * Returns a required direct child element of the given parent element.
     *
     * If the specified child does not exist directly below the parent,
     * an exception is thrown with a descriptive error message.
     *
     * @param parent the parent element in which the child is searched
     * @param tagName the name of the required child tag
     * @return the matching child element
     * @throws Exception if the child element is missing
     */
    private static Element requireDirectChild(Element parent, String tagName) throws Exception {
        // Try to find the requested direct child element
        Element child = getDirectChild(parent, tagName);

        // Throw an exception if the required child element is missing
        if (child == null) {
            throw new Exception("Missing <" + tagName + "> inside <" + parent.getTagName() + ">");
        }

        return child;
    }

    /**
     * Searches for a direct child element with the given tag name.
     *
     * Only direct child elements are considered, not nested descendants,
     * which makes the structural validation more precise.
     *
     * @param parent the parent element whose children are searched
     * @param tagName the tag name of the desired child element
     * @return the matching direct child element, or null if none exists
     */
    private static Element getDirectChild(Element parent, String tagName) {
        // Iterate over all direct child nodes of the parent element
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            // Only element nodes can match the requested tag name
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Return the first matching direct child element
                if (element.getTagName().equals(tagName)) {
                    return element;
                }
            }
        }

        // Return null if no matching direct child element was found
        return null;
    }
}
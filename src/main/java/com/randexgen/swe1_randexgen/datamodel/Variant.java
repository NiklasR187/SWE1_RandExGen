package com.randexgen.swe1_randexgen.datamodel;

/**
 * Represents a variant of a subtask.
 *
 * A variant contains the task description, the expected answer,
 * and the corresponding solution text.
 */
public class Variant {

    private String id;
    private String taskText;
    private String answerText;
    private String solutionText;

    /**
     * Creates an empty variant.
     */
    public Variant() {
    }

    /**
     * Creates a variant with all required fields.
     *
     * @param id the unique identifier of the variant
     * @param taskText the task description shown to the user
     * @param answerText the answer field content
     * @param solutionText the correct solution of the task
     */
    public Variant(String id, String taskText, String answerText, String solutionText) {
        this.id = id;
        this.taskText = taskText;
        this.answerText = answerText;
        this.solutionText = solutionText;
    }

    /**
     * Returns the identifier of the variant.
     *
     * @return the variant ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the task text of the variant.
     *
     * @return the task description
     */
    public String getTaskText() {
        return taskText;
    }

    /**
     * Returns the answer text of the variant.
     *
     * @return the answer content
     */
    public String getAnswerText() {
        return answerText;
    }

    /**
     * Returns the solution text of the variant.
     *
     * @return the solution content
     */
    public String getSolutionText() {
        return solutionText;
    }

    /**
     * Sets the identifier of the variant.
     *
     * @param id the variant ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the task text of the variant.
     *
     * @param taskText the task description
     */
    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    /**
     * Sets the answer text of the variant.
     *
     * @param answerText the answer content
     */
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    /**
     * Sets the solution text of the variant.
     *
     * @param solutionText the solution content
     */
    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    /**
     * Returns a string representation of the variant.
     *
     * The output includes the ID and task text for debugging purposes.
     *
     * @return a string representation of the variant
     */
    @Override
    public String toString() {
        return "Variant{" +
                "id='" + id + '\'' +
                ", taskText='" + taskText + '\'' +
                '}';
    }
}
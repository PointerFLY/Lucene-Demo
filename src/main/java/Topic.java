class Topic {

    private int id;
    private String description;
    private String narrative;

    static final String ID = "id";
    static final String DESCRIPTION = "description";
    static final String NARRATIVE = "narrative";

    Topic(int id, String author, String content) {
        this.id = id;
        this.description = author;
        this.narrative = content;
    }

    int getId() {
        return id;
    }

    String getDescription() {
        return description;
    }

    String getNarrative() {
        return narrative;
    }
}

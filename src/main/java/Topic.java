class Topic {

    private int id;
    private String title;
    private String description;
    private String narrative;

    static final String ID = "id";
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String NARRATIVE = "narrative";

    Topic(int id, String title, String author, String narrative) {
        this.id = id;
        this.title = title;
        this.description = author;
        this.narrative = narrative;
    }

    int getId() {
        return id;
    }

    String getTitle() { return title; }

    String getDescription() {
        return description;
    }

    String getNarrative() {
        return narrative;
    }
}

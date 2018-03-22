class Report {

    private String id;
    private String content;

    static final String ID = "id";
    static final String CONTENT = "content";

    Report(String id, String content) {
        this.id = id;
        this.content = content;
    }

    String getId() {
        return id;
    }

    String getContent() {
        return content;
    }
}

class Report {

    private String id;
    private String author;
    private String content;

    static final String ID = "id";
    static final String AUTHOR = "author";
    static final String CONTENT = "content";

    Report(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    String getId() {
        return id;
    }

    String getAuthor() {
        return author;
    }

    String getContent() {
        return content;
    }
}

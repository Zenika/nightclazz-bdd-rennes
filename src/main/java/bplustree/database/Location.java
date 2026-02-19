package bplustree.database;

public record Location(PageId pageId, Offset offset) {

    @Override
    public String toString() {
        return "page " + pageId.value() + ", offset " + offset.value();
    }
}

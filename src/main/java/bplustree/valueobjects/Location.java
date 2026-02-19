package bplustree.valueobjects;

public record Location(PageId pageId, Offset offset) {

    @Override
    public String toString() {
        return "RecordLocation[pageId=" + pageId.value() + ", offset=" + offset.value() + "]";
    }
}

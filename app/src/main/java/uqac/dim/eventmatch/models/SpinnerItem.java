package uqac.dim.eventmatch.models;

public class SpinnerItem {
    private int imageResId;
    private String text;

    public SpinnerItem(int imageResId, String text) {
        this.imageResId = imageResId;
        this.text = text;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getText() {
        return text;
    }
}

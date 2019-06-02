package com.sulzer.bookstore.constants;

        import java.util.HashMap;
        import java.util.Map;

public enum Category {
    LITERATURE(0),
    NONFICTION(1),
    ACTION(2),
    THRILLER(3),
    TECHNOLOGY(4),
    DRAMA(5),
    POETRY(6),
    MEDIA(7),
    OTHERS(8);

    private int value;
    private static Map map = new HashMap<>();

    private Category(int value) {
        this.value = value;
    }

    static {
        for (Category category : Category.values()) {
            map.put(category.value, category);
        }
    }

    public static Category valueOf(int category) {
        return (Category) map.get(category);
    }

    public int getValue() {
        return value;
    }
}

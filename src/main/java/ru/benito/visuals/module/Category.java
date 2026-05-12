package ru.benito.visuals.module;

/**
 * Категории модулей для ClickGUI.
 * Отображаемое имя — локализованный ключ (ru_ru.json).
 */
public enum Category {
    COMBAT  ("benito.category.combat"),
    VISUALS ("benito.category.visuals"),
    PLAYER  ("benito.category.player"),
    MISC    ("benito.category.misc"),
    FUNTIME ("benito.category.funtime");

    private final String translationKey;

    Category(String key) { this.translationKey = key; }

    public String getTranslationKey() { return translationKey; }
}

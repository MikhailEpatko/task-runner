# task-runner

### Версионирование

Что это: https://confluence.jsa-group.ru/pages/viewpage.action?pageId=102534564

Как: https://confluence.jsa-group.ru/pages/viewpage.action?pageId=127579031

### Автоматическое обновление версий зависимостей

1) При добавлении новой зависимости в build.gradle.kts, выполнить

```gradle refreshVersionsMigrate --mode=VersionsPropertiesOnly```

Плагин изменит содержимое build.gradle.kts

2) Провалидировать и исправить build.gradle.kts, если там будут ошибки
3) Выполнить

```gradle refreshVersions```

Плагин выполнит поиск новых версий зависимостей и укажет их в файле versions.properties.

4) Выбрать в versions.properties нужные версии зависимостей.
5) Для обновления версий без добавления новых зависимостей, нужно выполнить п.п.3, 4

# Checklist — Sprint 4

## Задача 1 — SearchActivity
- [x] Экран поиска сверстан по макету (Compose)
- [x] Используется TextField / OutlinedTextField
- [x] Есть placeholder «Поиск»
- [x] Иконка лупы слева присутствует
- [x] Иконка X справа очищает поле и скрывает результаты
- [x] Поиск по названию и исполнителю (заглушка)
- [x] Нажатие на лупу оставлено пустым

## Задача 2 — SettingsActivity
- [x] Экран сверстан по макету (Compose)
- [x] Названия кнопок и текстов в strings.xml
- [x] Иконка назад тёмная
- [x] Поделиться открывает системное меню шеринга
- [x] Написать разработчикам открывает почту с email, темой и телом из strings.xml
- [x] Пользовательское соглашение открывает браузер по ссылке из strings.xml

## Задача 3 — Навигация
- [x] Создан enum Screen (MAIN, SEARCH, SETTINGS)
- [x] Создан Composable PlaylistHost с NavController
- [x] В PlaylistHost NavHost содержит MainActivity / SearchActivity / SettingsActivity
- [x] Методы переходов (`navigateToMain`, `navigateToSearch`, `navigateToSettings`) реализованы в PlaylistHost
- [x] Кнопка Songs открывает SearchActivity
- [x] Кнопка Settings открывает SettingsActivity
- [x] Назад работает корректно (navigateUp)
- [x] Навигация работает без падений

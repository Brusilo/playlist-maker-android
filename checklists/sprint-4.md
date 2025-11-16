# Checklist — Sprint 4

## Задача 1 — SearchActivity
- [ ] Экран поиска сверстан по макету (Compose)
- [ ] Используется TextField / OutlinedTextField
- [ ] Есть placeholder «Поиск»
- [ ] Иконка лупы слева присутствует
- [ ] Иконка X справа очищает поле и скрывает результаты
- [ ] Поиск по названию и исполнителю (заглушка)
- [ ] Нажатие на лупу оставлено пустым

## Задача 2 — SettingsActivity
- [ ] Экран сверстан по макету (Compose)
- [ ] Названия кнопок и текстов в strings.xml
- [ ] Иконка назад тёмная
- [ ] Поделиться открывает системное меню шеринга
- [ ] Написать разработчикам открывает почту с email, темой и телом из strings.xml
- [ ] Пользовательское соглашение открывает браузер по ссылке из strings.xml

## Задача 3 — Навигация
- [ ] Создан enum Screen (MAIN, SEARCH, SETTINGS)
- [ ] Создан Composable PlaylistHost с NavController
- [ ] В PlaylistHost NavHost содержит MainActivity / SearchActivity / SettingsActivity
- [ ] Методы переходов (`navigateToMain`, `navigateToSearch`, `navigateToSettings`) реализованы в PlaylistHost
- [ ] Кнопка Songs открывает SearchActivity
- [ ] Кнопка Settings открывает SettingsActivity
- [ ] Назад работает корректно (navigateUp)
- [ ] Навигация работает без падений

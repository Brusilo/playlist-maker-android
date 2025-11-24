# Playlist Maker

Учебный проект по созданию мобильного приложения для поиска и управления плейлистами.

## Сборка
- Android Studio 2024.2.1+
- Min SDK 24, Target SDK 36
- Compose BOM 2025.11.00
- Kotlin 2.2.21
- Navigation Compose 2.9.6

## История разработки

### Sprint-3:
- Создан главный экран (MainActivity) с интерфейсом по макету Figma
- Реализованы кнопки навигации: Поиск, Плейлисты, Избранное, Настройки
- Добавлены переходы между экранами через Intent
- Созданы базовые активности SearchActivity и SettingsActivity

### Sprint-4:
- Переход на Single Activity архитектуру с Compose Navigation
- Создан навигационный граф с enum Screen и PlaylistHost
- Реализован экран поиска с TextField, иконками и функцией очистки
- Добавлен экран настроек с рабочими функциями: поделиться приложением, написать в поддержку, пользовательское соглашение

### Sprint-5:
- Внедрена Clean Architecture с разделением на слои Data, Domain, UI
- Создан эмулятор сервера (Storage) с тестовыми треками
- Реализованы DTO классы для передачи данных
- Добавлен репозиторий с преобразованием данных и обработкой ошибок
- Созданы интерфейсы NetworkClient и TracksRepository в domain слое

### Sprint-6:
- Реализован SearchViewModel с состояниями: Initial, Searching, Success, Fail
- Добавлен полнофункциональный поиск по названию трека и исполнителю
- Создан TrackListItem Composable для отображения результатов
- Интегрирована обработка ввода с клавиатуры (Enter для поиска)
- Реализована корректная обработка всех состояний UI с индикаторами загрузки и ошибок

## Чек-листы
- [Sprint-3](checklists/sprint-3.md)
- [Sprint-4](checklists/sprint-4.md)
- [Sprint-5](checklists/sprint-5.md)
- [Sprint-6](checklists/sprint-6.md)
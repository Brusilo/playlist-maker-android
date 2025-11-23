# Checklist — Sprint 6

## Задача 1 — SearchViewModel
- [x] Создан SearchViewModel с состояниями SearchState
- [x] Реализованы состояния: Initial, Searching, Success, Fail
- [x] Функция search() обрабатывает загрузку и ошибки
- [x] Создана фабрика ViewModel
- [x] Используется TracksRepository

## Задача 2 — SearchScreen (Compose)
- [x] Экран использует SearchViewModel
- [x] Обрабатываются все состояния SearchState
- [x] Initial - подсказка для пользователя
- [x] Searching - индикатор загрузки
- [x] Success - список треков в LazyColumn
- [x] Fail - сообщение об ошибке
- [x] Поиск по Enter (IME Action Search)
- [x] Очистка поля сбрасывает состояние

## Задача 3 — TrackListItem
- [x] Composable для отображения трека
- [x] Иконка, название, исполнитель, время
- [x] Иконка ic_music.png в ресурсах
- [x] Тексты в strings.xml

## Задача 4 — Архитектура
- [x] SearchViewModel использует Creator.getTracksRepository()
- [x] Сохранены слои: Data, Domain, UI
- [x] Репозиторий преобразует TrackDto в Track
- [x] Обработка ошибок IOException

## Задача 5 — Навигация
- [x] SearchScreen в NavHost
- [x] ViewModel создается через фабрику
- [x] Состояния сохраняются при повороте
- [x] Используется collectAsStateWithLifecycle

## Задача 6 — Функциональность
- [x] Поиск по треку и исполнителю
- [x] Задержка для эмуляции сети
- [x] Список треков при успехе
- [x] Сообщение при отсутствии результатов
- [x] Ошибки сети показываются пользователю

## Задача 7 — Code Quality
- [x] Только Material 3
- [x] Строки локализованы
- [x] Clean Architecture + MVVM
- [x] Проект компилируется
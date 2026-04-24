# Playlist Maker

Мобильное приложение для поиска музыки и управления плейлистами. Позволяет находить треки через iTunes API, создавать собственные плейлисты, добавлять треки в избранное и сохранять историю поиска.

## Требования для сборки

- **Android Studio:** 2025.2.1 (Otter) или новее
- **Kotlin:** 2.0.0 или новее
- **Min SDK:** 29 (Android 10)
- **Target SDK:** 36 (Android 14)
- **Compile SDK:** 36 (Android 14)
- **Compose BOM:** 2025.11.00

## Зависимости

Основные зависимости проекта:
- Jetpack Compose для UI
- Compose Navigation 2.7.7 для навигации
- Room для локальной базы данных
- DataStore для хранения настроек
- Retrofit 2.9.0 для работы с API
- Coil 2.5.0 для загрузки изображений
- OkHttp 4.12.0 для сетевых запросов

## Сборка и запуск

1. **Клонирование репозитория**
   ```bash
   git clone https://github.com/Brusilo/playlist-maker-android.git
   
2. **Открытие проекта в Android Studio**

- Откройте Android Studio версии 2025.2.1 или новее

- Выберите "Open" и укажите папку проекта, которая была создана после клонирования

3. **Синхронизация Gradle**

- Дождитесь завершения синхронизации зависимостей

- При необходимости обновите Gradle и зависимости

4. **Сборка проекта**

- Выберите сборку в меню Build → Make Project (Ctrl+F9)

- Убедитесь, что сборка завершена без ошибок

5. **Запуск на эмуляторе или устройстве**

- Подключите Android-устройство с Android 10+ и включенной отладкой по USB

- Или создайте/запустите эмулятор Android 10+ в AVD Manager

- Нажмите Run → Run 'app' (Shift+F10)

## Чек-листы
- [Final-Project-Checklist](checklists/Final-Project-Checklist.md)
- [Sprint-3](checklists/sprint-3.md)
- [Sprint-4](checklists/sprint-4.md)
- [Sprint-5](checklists/sprint-5.md)
- [Sprint-6](checklists/sprint-6.md)
- [Sprint-7](checklists/sprint-7.md)
- [Sprint-8](checklists/sprint-8.md)
- [Sprint-9](checklists/sprint-9.md)
- [Sprint-10](checklists/sprint-10.md)
- [Sprint-12](checklists/sprint-12.md)

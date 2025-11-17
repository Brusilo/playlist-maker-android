# Checklist — Sprint 5

## Задача 1. Эмуляция сервера
- [x] Создан класс Storage для хранения списка тестовых треков
- [x] В Storage добавлены заранее подготовленные треки
- [x] Создана эмитация работы сервера

## Задача 2. Слой Data
- [x] Создан пакет data с подпапками dto и network
- [x] Созданы DTO-классы:
    - [x] TrackDto
    - [x] TracksSearchRequest
    - [x] TracksSearchResponse
    - [x] BaseResponse
- [x] DTO корректно отражают структуру ответа «сервера»
- [x] Создан класс Track в data.network
- [x] Создан RetrofitNetworkClient (эмуляция поверх Storage)
- [x] Клиент реализует интерфейс NetworkClient (из domain)

## Задача 3. Слой Domain
- [x] Создан интерфейс NetworkClient
- [x] Создан интерфейс TracksRepository
- [x] Описаны сигнатуры методов searchTracks()
- [x] Интерфейсы не зависят от Android и библиотек Retrofit/Coroutines

## Задача 4. Репозиторий
- [x] Создан класс TracksRepositoryImpl в пакете data.network
- [x] Репозиторий преобразует DTO в доменную модель Track
- [x] Репозиторий корректно обрабатывает:
    - [x] Успех
    - [x] Ошибки
    - [x] Пустые результаты поиска
- [x] Репозиторий использует NetworkClient для выполнения запросов
- [x] Логика соответствует принципам Clean Architecture

## Задача 5. Архитектурный каркас
- [x] Все слои (Data — Domain) созданы и подключены
- [x] Приложение компилируется и работает без ошибок
- [x] Репозиторий готов для использования во ViewModel в следующем спринте

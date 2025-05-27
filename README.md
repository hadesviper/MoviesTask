# 🎬 Movie App (MVI Clean Architecture)

A Kotlin-based Android movie app using **Jetpack Compose** and **MVI Clean Architecture**. It demonstrates multiple caching strategies, paging support, and clear separation of concerns using **Clean Architecture principles**.

---

## 🖥️ Screens Overview

The main screen consists of **two tabs**:

### 🔹 Popular Movies (Tab 1)
- Supports **infinite scrolling** using `Paging 3`
- Fetches data via **Retrofit**
- Implements **HTTP header caching** for efficient network usage
- Intended for paginated content loading

### 🔹 Top Rated Movies (Tab 2)
- Loads a **single page** of top-rated movies
- Uses **Room database** for offline caching
- Caching flow follows task instructions:
  - Tries local data first, and present it if it exists
  - Perform a network call and replace the presented data
  - Saves new data to cache

### 📌 Why two caching strategies?
The task description did not specify:
- Whether to load all pages or just a single page
- Which caching approach to use (Room or HTTP)

So this app implements **both**:
- `Room` caching with single-page fetch for Top Rated
- `HTTP + Paging 3` for Popular, with standard HTTP caching

This showcases flexibility in architectural thinking while meeting potential expectations.

---

## 🧰 Tech Stack

| Layer         | Tools/Libraries                            |
|---------------|---------------------------------------------|
| UI            | Jetpack Compose, MVI                       |
| Architecture  | Clean Architecture                        |
| Network       | Retrofit, OkHttp                           |
| Caching       | Room (Top Rated), HTTP Header (Popular)   |
| DI            | Dagger Hilt                                |
| Paging        | Paging 3 (Popular tab only)               |
| Testing       | JUnit, MockK, Coroutines Test             |

---

## ✅ Features

- 🧠 MVI pattern with side effects, state, and intents
- ✨ Jetpack Compose UI
- 📡 Retrofit HTTP requests
- 🗃️ Room database for persistent cache
- 🔄 Paging 3 support for popular movies
- ⚡ Caching: Room + HTTP headers
- ✅ Unit tests for each use case

---

## 🧪 Unit Testing

All use cases are **unit-tested** using `MockK` and `kotlinx.coroutines.test`.

Examples:
- `FetchPagedMoviesUseCaseTest`
- `FetchCachedMoviesUseCaseTest`
- `CacheMoviesUseCaseTest`
- `DeleteCacheUseCaseTest`

---

## 📲 Download

You can download the APK directly [here](https://github.com/hadesviper/MoviesTask/releases/download/v1.0/app-debug.apk)

---

## 👨‍💻 Author

**Ibrahim Abdin**

- GitHub: [GitHub](https://github.com/hadesviper)
- LinkedIn: [LinkedIn](https://linkedin.com/in/ibrahim-abdin-7ab463169)



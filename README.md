# 📚 BookNest – Android Book Tracker App

**BookNest** is a modern Android app that helps users organize and track their personal reading journeys. Whether you're managing your "To Be Read" list or logging your progress through a current novel, BookNest makes it easy and enjoyable to stay on top of your reading goals.

## ✅ Features

- **Add, edit, and delete book entries**  
- **Track your reading progress** using a visual progress bar  
- **Categorize books** into "Reading," "Finished," or "Wishlist"  
- **Rate and review books** after reading  
- **Save favorite quotes** under each book entry  
- **View basic reading stats** to stay motivated

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **Database:** Room (SQLite)
- **Libraries:** LiveData, ViewModel, RecyclerView
- **Build System:** Gradle

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/booknest.git
cd booknest
```

### 2. Open in Android Studio
- Launch Android Studio
- Open the booknest project folder
- Sync Gradle and build the project

### 3. Run the App
- Connect your Android device or use an emulator
- Press Run ▶ to install and launch the app

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests in Android Studio
- **All tests:** Right-click on `app/src/test` folder → Run 'All Tests'
- **Specific test:** Right-click on test file (e.g., `BookTest.kt`) → Run 'BookTest'
- **Single method:** Click the green arrow next to the test method

Test results appear in the Run window and HTML reports are generated at `app/build/reports/tests/`

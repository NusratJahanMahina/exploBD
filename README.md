
# ExploBD — Explore Bangladesh

Our 2nd year OOP group project. The goal was to make a travel companion app specifically for Bangladesh — something useful for both locals and foreigners, solo travellers and groups.

---

## The Idea

The original vision was big. A complete travel guide for Bangladesh that covers everything someone might need:

- Destination explorer by division
- Emergency services nearby
- Local translator
- Maps and navigation
- Famous local food guides
- Ride/transport options
- Travel checklists
- Group trip planning
- Expense splitting
- Recommendations based on your travel style

Basically one app where you don't need to open 10 different things just to plan a trip. Designed to be useful for foreigners visiting Bangladesh just as much as locals exploring their own country.

But since this was an OOP course project and we couldn't use external APIs or paid services, a lot of those features had to be cut. So we focused on what we could build properly with pure Java and a local SQLite database.

---

## What we actually built

- Browse tourist destinations across all divisions — Barishal, Chittagong, Khulna, Mymensingh, Rajshahi, Rangpur, Sylhet — with photos
- User accounts — register, login, edit profile
- Friends — add friends, send and receive friend requests
- Travel groups — create a group, invite friends, vote on destinations
- Destination recommendations based on travel style
- Expense splitting among group members
- Trip checklist
- Messaging inside groups
- Notifications
- Log completed trips

It's not everything we planned but we tried to make the core of it work well.

---

## Tech used

- Java with Swing for the desktop UI
- SQLite as the local database
- sqlite-jdbc to connect Java to SQLite
- AbsoluteLayout (NetBeans GUI library)
- Built in NetBeans IDE

---

## Folder structure

```
ExploBD/
├── src/            → all the Java code
├── data/           → the SQLite database file
├── lib/            → the JAR files needed to run
│   ├── sqlite-jdbc-3.51.1.0.jar
│   └── AbsoluteLayout.jar
└── nbproject/      → NetBeans project settings
```

---

## How to run this

You'll need Java JDK 8 or above and NetBeans installed.

1. Clone the repo
2. Open NetBeans → File → Open Project → select the folder
3. Go to Project Properties → Libraries → remove any broken references → Add JAR/Folder → add both JARs from the `lib/` folder
4. Make sure `DatabaseConnection.java` points to the `.db` file inside the `data/` folder
5. Clean and Build, then run

If something breaks it's probably the library paths — just re-add the JARs from `lib/` and it should be fine.

---

## Team

**Nusrat Jahan Mahina -**
Core of the project — destination explorer, travel groups, user system, checklist, messaging, notifications, database design, and overall architecture

**Sanzida Afroz -**
Friends system — add friends, send and receive friend requests, expense splitting

**Nazifa Rahman -**
Recommendation system — destination suggestions based on travel style

---

2nd year OOP course project, 2026

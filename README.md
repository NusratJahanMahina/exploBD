# ExploBD — Explore Bangladesh

A desktop app to help people explore and plan trips around Bangladesh. Built with Java Swing and SQLite for our 2nd year OOP course.

---

## The original idea

The goal was to build a complete travel companion app for Bangladesh — something that holds your hand through the entire trip from start to finish. You open the app, pick where you want to go, get a full guide of that place, find out what food to try, how to get there, what to do in an emergency, how to communicate if you don't speak Bangla, plan it with your friends, split the costs, and track everything in one place. No switching between apps, no searching around. Just one app that covers the whole journey.

---

## Why it ended up smaller

This was an OOP course project so the main focus was applying OOP concepts properly, not building a full product. We also couldn't use any external APIs — no maps, no live data, no translation services. Anything that needed internet or a third party service had to go. So we stripped it down to what we could build completely on our own using just Java and a local SQLite database.

---

## What we built

- Browse tourist spots by division — Barishal, Chittagong, Khulna, Mymensingh, Rajshahi, Rangpur, Sylhet — with photos
- User accounts — register, login, edit profile
- Friends — add friends, send and receive friend requests
- Travel groups — create a group, invite friends, vote on destinations
- Recommendations based on travel style
- Expense splitting among group members
- Trip checklist
- Messaging inside groups
- Notifications
- Log completed trips

---

## How to run

You'll need Java JDK 8 or above and NetBeans.

1. Clone or download the repo
2. Open NetBeans → File → Open Project → select the folder
3. Go to Project Properties → Libraries → remove broken references → Add JAR/Folder → add both JARs from the `lib/` folder
4. Check that `DatabaseConnection.java` points to the `.db` file in the `data/` folder
5. Clean and Build → Run

If it breaks, it's most likely the library paths — re-add the JARs from `lib/` and it should work.

---

## Team

**Nusrat Jahan Mahina** — overall architecture, database design, destination explorer, user system, travel groups, checklist, messaging, notifications

**Sanzida Afroz** — friends system, expense splitting

**Nazifa Rahman** — recommendation system

---

2nd year OOP course project, 2026

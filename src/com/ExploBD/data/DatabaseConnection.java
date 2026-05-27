package com.ExploBD.data;

import java.sql.*;
import javax.swing.JOptionPane;
import java.io.File;

public class DatabaseConnection {
//
//    private static final String DB_PATH = "C:\\Users\\Mahina\\OneDrive\\Desktop\\ExploBD_2\\ExploBD_2\\user_info.db";
//    private static final String DATABASE_URL = "jdbc:sqlite:" + DB_PATH;
//    private static final String DB_DIRECTORY = "C:\\Users\\Mahina\\OneDrive\\Desktop\\ExploBD_2\\ExploBD_2";
    
    // This will look for the database inside a "data" folder in your project directory
private static final String DB_DIRECTORY = "data";
private static final String DB_PATH = DB_DIRECTORY + File.separator + "user_info.db";
private static final String DATABASE_URL = "jdbc:sqlite:" + DB_PATH;
    
    private static boolean tablesCreated = false;

    public static final String[] SECURITY_QUESTIONS = {
        "What is your birth month?",
        "What was the name of your first pet?",
        "What is the name of the town where you were born?"
    };

    /* public static synchronized Connection getConnection() {
        try {
            File dbDir = new File(DB_DIRECTORY);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            Connection conn = DriverManager.getConnection(DATABASE_URL);

            if (!tablesCreated) {
                createTables(conn);
                tablesCreated = true;
            }

            return conn;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database Error: " + e.getMessage() + "\nPath: " + DB_PATH,
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }*/
    //sristy did this
    public static synchronized Connection getConnection() {
        try {
            // EXPLICITLY LOAD THE DRIVER
            Class.forName("org.sqlite.JDBC");

            File dbDir = new File(DB_DIRECTORY);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            System.out.println("Attempting to connect to: " + DATABASE_URL);
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection successful!");

            if (!tablesCreated) {
                createTables(conn);
                tablesCreated = true;
            }

            return conn;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "SQLite JDBC Driver not found!\n"
                    + "Please download sqlite-jdbc.jar and add it to project libraries.\n"
                    + "Error: " + e.getMessage(),
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database Error: " + e.getMessage() + "\nPath: " + DB_PATH,
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    private static void createTables(Connection conn) {
        Statement stmt = null;

        String createUsersTable = """
    CREATE TABLE IF NOT EXISTS users (
        user_id TEXT PRIMARY KEY,
        username TEXT NOT NULL,
        email TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        security_question TEXT NOT NULL,
        security_answer TEXT NOT NULL,
        full_name TEXT,
        nationality TEXT DEFAULT 'Bangladeshi',
        phone TEXT,
        emergency_contact TEXT,
        nid_passport TEXT,
        date_of_birth TEXT,
        gender TEXT,
        min_budget INTEGER DEFAULT 500,
        max_budget INTEGER DEFAULT 10000,
        profile_image_path TEXT,
        travel_styles TEXT,
        favorite_places TEXT,
        wishlist_places TEXT,
        is_leader BOOLEAN DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""";

        String createPlacesTable = """
    CREATE TABLE IF NOT EXISTS places (
        place_id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        division TEXT NOT NULL,
        district TEXT NOT NULL,
        category TEXT NOT NULL CHECK(category IN ('HISTORICAL', 'NATURE', 'ENTERTAINMENT')),
        address TEXT,
        description TEXT,
        entry_cost REAL DEFAULT 0,
        best_time TEXT,
        why_visit TEXT,
        image_path TEXT,
        tags TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
""";

        String createHistoricalPlacesTable = """
    CREATE TABLE IF NOT EXISTS historical_places (
        place_id TEXT PRIMARY KEY,
        year_built TEXT,
        time_period TEXT,
        architecture_style TEXT,
        historical_facts TEXT,
        is_unesco BOOLEAN DEFAULT 0,
        image1 TEXT, -- 2nd image
        image2 TEXT, -- 3rd image
        image3 TEXT, -- 4th image
        image4 TEXT, -- 5th image
        image5 TEXT, -- 6th image
        FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
    )
""";

        String createNaturePlacesTable = """
    CREATE TABLE IF NOT EXISTS nature_places (
        place_id TEXT PRIMARY KEY,
        nature_type TEXT,
        area_size TEXT,
        activities TEXT,
        conservation_status TEXT,
        image1 TEXT, -- 2nd image
        image2 TEXT, -- 3rd image
        image3 TEXT, -- 4th image
        image4 TEXT, -- 5th image
        image5 TEXT, -- 6th image
        FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
    )
""";

        String createEntertainmentPlacesTable = """
    CREATE TABLE IF NOT EXISTS entertainment_places (
        place_id TEXT PRIMARY KEY,
        entertainment_type TEXT,
        opening_hours TEXT,
        special_events TEXT,
        average_spending REAL,
        contact_info TEXT,
        has_entry_fee BOOLEAN DEFAULT 1,
        image1 TEXT, 
        image2 TEXT, 
        image3 TEXT, 
        image4 TEXT, 
        image5 TEXT, 
        FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
    )
""";

        String createGroupsTable = """
    CREATE TABLE IF NOT EXISTS groups (
        group_id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        description TEXT,
        leader_id TEXT NOT NULL,
        destination_name TEXT,
        destination_division TEXT,
        min_budget INTEGER,
        max_budget INTEGER,
        max_members INTEGER,
        start_date TEXT,
        end_date TEXT,
        destination_image_path TEXT,
        status TEXT CHECK(status IN ('DRAFT', 'PLANNING', 'VOTING', 'CONFIRMED')),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        invitation_code TEXT UNIQUE,
        voting_start_time TEXT,
        voting_end_time TEXT,
        FOREIGN KEY (leader_id) REFERENCES users(user_id)
    )
""";
        String createGroupMembersTable = """
    CREATE TABLE IF NOT EXISTS group_members (
        group_id TEXT,
        member_id TEXT,
        member_type TEXT CHECK(member_type IN ('REGISTERED', 'GUEST')),
        role TEXT CHECK(role IN ('LEADER', 'MEMBER', 'PENDING')),
        joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        invited_by TEXT,
        invitation_code TEXT,
        PRIMARY KEY (group_id, member_id),
        FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE
    )
""";
        String createInvitationsTable = """
CREATE TABLE IF NOT EXISTS invitations (
    invitation_id TEXT PRIMARY KEY,
    group_id TEXT,
    inviter_id TEXT,
    invitee_email TEXT,
    invitee_name TEXT,
    type TEXT CHECK(type IN ('EMAIL', 'PLACEHOLDER')),
    status TEXT CHECK(status IN ('PENDING', 'APPROVED', 'SENT', 'ACCEPTED', 'DECLINED')),
    needs_leader_approval BOOLEAN DEFAULT 0,
    sent_date TEXT,       
    expiry_date TEXT,     
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invitation_code TEXT, 
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_id) REFERENCES users(user_id)
)
""";

        String createMessagesTable = """
CREATE TABLE IF NOT EXISTS messages (
    message_id TEXT PRIMARY KEY,
    group_id TEXT NOT NULL,
    sender_id TEXT NOT NULL,
    message_type TEXT CHECK(message_type IN ('SUGGESTION', 'CHAT', 'BULLETIN', 'INVITE_REQUEST')),
    status TEXT CHECK(status IN ('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'ARCHIVED')),
    title TEXT,
    content TEXT,
    like_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Suggestion specific
    place_id TEXT,
    place_name TEXT,
    upvotes INTEGER DEFAULT 0,
    downvotes INTEGER DEFAULT 0,
    reason TEXT,
    
    -- Invite request specific
    invitee_name TEXT,
    invitee_email TEXT,
    request_type TEXT,
    
    
    is_important BOOLEAN DEFAULT 0,
    
   
    reviewed_by TEXT,
    reviewed_at TIMESTAMP,
    
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id) ON DELETE SET NULL
)
""";

        String createMessageIndexes = """
CREATE INDEX IF NOT EXISTS idx_messages_group ON messages(group_id);
CREATE INDEX IF NOT EXISTS idx_messages_status ON messages(status);
CREATE INDEX IF NOT EXISTS idx_messages_type ON messages(message_type);
""";

        String createNotificationsTable = """
    CREATE TABLE IF NOT EXISTS notifications (
        notification_id TEXT PRIMARY KEY,
        user_id TEXT,
        type TEXT,
        title TEXT,
        message TEXT,
        related_id TEXT,
        is_read BOOLEAN DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(user_id)
    )
""";

        String createFriendRequestsTable = """
    CREATE TABLE IF NOT EXISTS friend_requests (
        request_id TEXT PRIMARY KEY,
        from_user_id TEXT NOT NULL,
        to_user_id TEXT NOT NULL,
        status TEXT CHECK(status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
        sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";

        String createFriendshipsTable = """
    CREATE TABLE IF NOT EXISTS friendships (
        friendship_id TEXT PRIMARY KEY,
        user1_id TEXT NOT NULL,
        user2_id TEXT NOT NULL,
        since TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user1_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (user2_id) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";

        String createUserPreferencesTable = """
    CREATE TABLE IF NOT EXISTS user_preferences (
        user_id TEXT NOT NULL,
        place_id TEXT NOT NULL,
        preference_type TEXT CHECK(preference_type IN ('FAVORITE', 'WISHLIST')),
        added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id, place_id, preference_type),
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
    )
""";
        String createSuggestionVotesTable = """
CREATE TABLE IF NOT EXISTS suggestion_votes (
    vote_id TEXT PRIMARY KEY,
    suggestion_id TEXT NOT NULL,
    voter_id TEXT NOT NULL,
    vote_type TEXT CHECK(vote_type IN ('YES', 'NO')),
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (suggestion_id) REFERENCES messages(message_id) ON DELETE CASCADE,
    FOREIGN KEY (voter_id) REFERENCES users(user_id),
    UNIQUE(suggestion_id, voter_id)
)
""";

        //SRISTY DID this part
        String createToursTable = """
    CREATE TABLE IF NOT EXISTS tours (
        tour_id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        description TEXT,
        start_date TEXT,
        end_date TEXT,
        is_active INTEGER DEFAULT 1,
        created_by TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";

        String createTourParticipantsTable = """
    CREATE TABLE IF NOT EXISTS tour_participants (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tour_id TEXT NOT NULL,
        user_id TEXT NOT NULL,
        role TEXT CHECK(role IN ('LEADER', 'MEMBER')) DEFAULT 'MEMBER',
        joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (tour_id) REFERENCES tours(tour_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        UNIQUE(tour_id, user_id)
    )
""";

        String createExpensesTable = """
    CREATE TABLE IF NOT EXISTS expenses (
        expense_id TEXT PRIMARY KEY,
        tour_id TEXT NOT NULL,
        paid_by TEXT NOT NULL,
        amount REAL NOT NULL,
        description TEXT,
        expense_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (tour_id) REFERENCES tours(tour_id) ON DELETE CASCADE,
        FOREIGN KEY (paid_by) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";

        String createExpenseSplitsTable = """
    CREATE TABLE IF NOT EXISTS expense_splits (
        split_id TEXT PRIMARY KEY,
        expense_id TEXT NOT NULL,
        user_id TEXT NOT NULL,
        share_amount REAL NOT NULL,
        is_settled INTEGER DEFAULT 0,
        settled_at TIMESTAMP,
        FOREIGN KEY (expense_id) REFERENCES expenses(expense_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";
// Add after your existing table creations
        String createTourChecklistTable = """
    CREATE TABLE IF NOT EXISTS tour_checklist (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tour_id TEXT NOT NULL,
        item_text TEXT NOT NULL,
        created_by TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (tour_id) REFERENCES tours(tour_id) ON DELETE CASCADE,
        FOREIGN KEY (created_by) REFERENCES users(user_id)
    )
""";

        String createTourChecklistProgressTable = """
    CREATE TABLE IF NOT EXISTS tour_checklist_progress (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        checklist_id INTEGER NOT NULL,
        user_id TEXT NOT NULL,
        completed BOOLEAN DEFAULT 0,
        completed_at TIMESTAMP,
        FOREIGN KEY (checklist_id) REFERENCES tour_checklist(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        UNIQUE(checklist_id, user_id)
    )
""";

        String createTourSettlementsTable = """
    CREATE TABLE IF NOT EXISTS tour_settlements (
        settlement_id TEXT PRIMARY KEY,
        tour_id TEXT NOT NULL,
        from_user_id TEXT NOT NULL,
        to_user_id TEXT NOT NULL,
        amount REAL NOT NULL,
        status TEXT CHECK(status IN ('PENDING', 'PAID')) DEFAULT 'PENDING',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        paid_at TIMESTAMP,
        FOREIGN KEY (tour_id) REFERENCES tours(tour_id) ON DELETE CASCADE,
        FOREIGN KEY (from_user_id) REFERENCES users(user_id),
        FOREIGN KEY (to_user_id) REFERENCES users(user_id)
    )
""";

        //SRISTY DID THIS 
        String createCompletedToursTable = """
    CREATE TABLE IF NOT EXISTS completed_tours (
        completed_id TEXT PRIMARY KEY,
        tour_id TEXT NOT NULL,
        group_id TEXT,
        tour_name TEXT NOT NULL,
        destination_name TEXT,
        destination_division TEXT,
        start_date TEXT,
        end_date TEXT,
        total_expenses REAL DEFAULT 0,
        member_count INTEGER DEFAULT 0,
        budget_min INTEGER,
        budget_max INTEGER,
        completed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        expense_summary TEXT,
        FOREIGN KEY (tour_id) REFERENCES tours(tour_id) ON DELETE CASCADE,
        FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE SET NULL
    )
""";

        String createCompletedTourParticipantsTable = """
    CREATE TABLE IF NOT EXISTS completed_tour_participants (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        completed_id TEXT NOT NULL,
        user_id TEXT NOT NULL,
        user_name TEXT NOT NULL,
        user_email TEXT,
        amount_owed REAL DEFAULT 0,
        amount_paid REAL DEFAULT 0,
        FOREIGN KEY (completed_id) REFERENCES completed_tours(completed_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    )
""";

        try {
            stmt = conn.createStatement();
            stmt.execute(createUsersTable);
            stmt.execute(createPlacesTable);
            stmt.execute(createHistoricalPlacesTable);
            stmt.execute(createNaturePlacesTable);
            stmt.execute(createEntertainmentPlacesTable);
            stmt.execute(createGroupsTable);
            stmt.execute(createGroupMembersTable);
            stmt.execute(createInvitationsTable);
            stmt.execute(createNotificationsTable);
            stmt.execute(createFriendRequestsTable);
            stmt.execute(createFriendshipsTable);
            stmt.execute(createUserPreferencesTable);

            stmt.execute(createMessagesTable);
            stmt.execute(createMessageIndexes);
            stmt.execute(createSuggestionVotesTable);
            stmt.execute(createToursTable);
            stmt.execute(createTourParticipantsTable);
            stmt.execute(createExpensesTable);
            stmt.execute(createExpenseSplitsTable);
            stmt.execute(createTourSettlementsTable);
// Execute these statements
//SRISTY
            stmt.execute(createCompletedToursTable);
            stmt.execute(createCompletedTourParticipantsTable);
//till here
            stmt.execute(createTourChecklistTable);
            stmt.execute(createTourChecklistProgressTable);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getDatabasePath() {
        return DB_PATH;
    }

}

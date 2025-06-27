// Change the username and password of Database inside the DB CLASS
// make sure you have mysql connector version 9.2.0 and the class path is set

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;	
import java.sql.*;

// Custom label class with comic font and color
class FancyLabel extends JLabel {
    // Constructor to style label
    public FancyLabel(String text, int size, boolean bold) {
        super(text, SwingConstants.CENTER); // Center text
        setFont(new Font("Comic Sans MS", bold ? Font.BOLD : Font.PLAIN, size)); // Set font style and size
        setForeground(new Color(48, 70, 134)); // Set text color
    }
}

// Custom button class with comic font and background color
class FancyButton extends JButton {
    // Constructor to style button
    public FancyButton(String text) {
        super(text); // Set button text
        setFont(new Font("Comic Sans MS", Font.BOLD, 16)); // Set font style and size
        setBackground(new Color(140, 180, 230)); // Set background color
        setForeground(new Color(30, 40, 60)); // Set text color
        setFocusPainted(false); // Remove focus border
        setBorder(BorderFactory.createLineBorder(new Color(90, 120, 180), 2)); // Set border color and thickness
    }
}

// Custom text field class with comic font and color
class FancyField extends JTextField {
    // Constructor to style text field
    public FancyField(int cols) {
        super(cols); // Set columns
        setFont(new Font("Comic Sans MS", Font.PLAIN, 16)); // Set font style and size
        setBackground(new Color(225, 235, 245)); // Set background color
        setForeground(new Color(48, 70, 134)); // Set text color
        setBorder(BorderFactory.createLineBorder(new Color(90, 120, 180), 1)); // Set border color and thickness
    }
}

// Custom panel class with background color and GridBagLayout
class FancyPanel extends JPanel {
    // Constructor to style panel
    public FancyPanel() {
        super(new GridBagLayout()); // Use GridBagLayout
        setBackground(new Color(242, 247, 255)); // Set background color
    }
}

// Database helper class for all DB operations
class DB {
    static final String URL = "jdbc:mysql://localhost:3306/finance_tracker"; // Database URL
    static final String USER = "root"; // Database username
    static final String PASS = "goldfish"; // Database password

    // Get database connection
    public static Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver"); // Load driver
        return DriverManager.getConnection(URL, USER, PASS); // Connect to DB
    }

    // Insert a new user in the database, return their user id
    public static int insertUser(String name, double weeklyMoney) throws Exception {
        double initialSavings = 0.2 * weeklyMoney; // 20% of money to savings
        double usableMoney = 0.8 * weeklyMoney; // 80% for spending
        Connection c = getConn(); // Get DB connection
        String sql = "INSERT INTO users (name, weekly_pocket_money, savings) VALUES (?, ?, ?)";
        PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // Prepare SQL statement
        ps.setString(1, name);
        ps.setDouble(2, usableMoney);
        ps.setDouble(3, initialSavings);
        ps.executeUpdate(); // Execute SQL
        ResultSet rs = ps.getGeneratedKeys(); // Get generated user id
        int id = -1;
        if (rs.next()) id = rs.getInt(1);
        rs.close();
        ps.close();
        c.close();
        return id;
    }

    // Get weekly money for a user from DB
    public static double getWeeklyMoney(int userId) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "SELECT weekly_pocket_money FROM users WHERE id=?";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        double money = 0;
        if (rs.next()) money = rs.getDouble(1); // Get weekly money
        rs.close();
        ps.close();
        c.close();
        return money;
    }

    // Get savings for a user from DB
    public static double getSavings(int userId) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "SELECT savings FROM users WHERE id=?";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        double s = 0;
        if (rs.next()) s = rs.getDouble(1); // Get savings
        rs.close();
        ps.close();
        c.close();
        return s;
    }

    // Add an expense for a user in the DB
    public static void addExpense(int userId, int dayNum, String category, double amount) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "INSERT INTO expenses (user_id, expense_date, day_num, category, amount) VALUES (?, CURDATE(), ?, ?, ?)";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, dayNum);
        ps.setString(3, category);
        ps.setDouble(4, amount);
        ps.executeUpdate(); // Insert expense
        ps.close();
        c.close();
    }

    // Get total spent by a user today
    public static double getTotalSpentToday(int userId, int dayNum) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "SELECT SUM(amount) FROM expenses WHERE user_id=? AND day_num=? AND expense_date=CURDATE()";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, dayNum);
        ResultSet rs = ps.executeQuery();
        double total = 0;
        if (rs.next()) total = rs.getDouble(1); // Get total spent
        rs.close();
        ps.close();
        c.close();
        return total;
    }

    // Add leftover money to savings for a user
    public static void addToSavings(int userId, double amount) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "UPDATE users SET savings = savings + ? WHERE id=?";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setDouble(1, amount);
        ps.setInt(2, userId);
        ps.executeUpdate(); // Update savings
        ps.close();
        c.close();
    }

    // Get sum of a category for a user
    public static double sumCategory(int userId, String category) throws Exception {
        Connection c = getConn(); // Get DB connection
        String sql = "SELECT SUM(amount) FROM expenses WHERE user_id=? AND category=?";
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setString(2, category);
        ResultSet rs = ps.executeQuery();
        double total = 0;
        if (rs.next()) total = rs.getDouble(1); // Get sum
        rs.close();
        ps.close();
        c.close();
        return total;
    }
}

// Welcome screen for the application
class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("Welcome to FinanceTracker"); // Set window title
        setSize(500, 530); // Set window size
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app on exit
        setLocationRelativeTo(null); // Center window

        // Multi-line instruction text
        String instructionsText =
                "Welcome to FinanceTracker\n\n" +
                "Manage your weekly pocket money!\n\n" +
                "How it works:\n" +
                "- Enter name and money.\n" +
                "- Record daily spending (Needs, Fun, Saved).\n" +
                "- See weekly summary.\n\n" +
                "50-30-20 Rule:\n" +
                "- 50% Needs\n" +
                "- 30% Fun\n" +
                "- 20% Savings\n\n" +
                "By Khadija and Sultan";

        JTextArea instruction = new JTextArea(instructionsText); // Instruction text area
        instruction.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        instruction.setForeground(new Color(76, 0, 120));
        instruction.setLineWrap(true);
        instruction.setWrapStyleWord(true);
        instruction.setOpaque(false);
        instruction.setEditable(false);

        FancyButton createAccountBtn = new FancyButton("Create Account"); // Button to create account
        // Event when button is clicked
        createAccountBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CreateAccountFrame(); // Open create account screen
                dispose(); // Close welcome screen
            }
        });

        JPanel panel = new JPanel(); // Main panel
        panel.setBackground(new Color(242, 247, 255));
        panel.setLayout(new BorderLayout(0, 20)); // Use BorderLayout
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        panel.add(instruction, BorderLayout.CENTER); // Add instructions in the center
        panel.add(createAccountBtn, BorderLayout.SOUTH); // Add button at bottom

        add(panel); // Add panel to frame
        setVisible(true); // Show window
    }
}

// Frame for creating a new account
class CreateAccountFrame extends JFrame implements ActionListener {
    FancyField nameF, moneyF; // Input fields for name and money
    FancyButton create, backBtn; // Buttons to create and go back

    CreateAccountFrame() {
        setTitle("Create Account - Pocket Money Tracker"); // Set window title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app on exit
        setSize(410, 340); // Set window size
        setLocationRelativeTo(null); // Center window

        FancyPanel p = new FancyPanel(); // Use custom panel

        FancyLabel l1 = new FancyLabel("Create Your Account", 23, true); // Title label

        nameF = new FancyField(11); // Name input field
        moneyF = new FancyField(11); // Money input field

        create = new FancyButton("Create Account"); // Create account button
        create.addActionListener(this);

        backBtn = new FancyButton("Back"); // Back button
        backBtn.addActionListener(this);

        add(p); // Add panel to frame
        addToPanel(p, l1, 0); // Add title
        addToPanel(p, new FancyLabel("Your Name:", 16, false), 1); // Add name label
        addToPanel(p, nameF, 2); // Add name field
        addToPanel(p, new FancyLabel("Weekly Pocket Money (Rs):", 16, false), 3); // Add money label
        addToPanel(p, moneyF, 4); // Add money field
        addToPanel(p, create, 5); // Add create button
        addToPanel(p, backBtn, 6); // Add back button

        setVisible(true); // Show window
    }

    // Method to help add components to the panel at the right position
    public void addToPanel(JPanel p, JComponent c, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 5, 40); // Padding
        p.add(c, gbc);
    }

    // Handle button actions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == create) {
            String name = nameF.getText().trim(); // Get name
            double money = 0;
            try {
                money = Double.parseDouble(moneyF.getText().trim()); // Get money
            } catch (Exception ex) {}
            if (name.equals("") || money <= 0) { // Check for valid input
                JOptionPane.showMessageDialog(this, "Enter a valid name and weekly money!");
                return;
            }
            try {
                int userId = DB.insertUser(name, money); // Insert user to DB
                if (userId > 0) {
                    double total = money;
                    double savings = 0.2 * total;
                    double needs = 0.5 * total;
                    double fun = 0.3 * total;
                    double spendable = needs + fun; // 80% of total

                    // Show summary of budget to user
                    String msg =
                        "Hello " + name + ", your account has been created!\n\n" +
                        "Your weekly pocket money is Rs. " + (int)total + ".\n\n" +
                        "Here's your personalized budget:\n" +
                        "- Savings (20%): Rs. " + (int)savings + " (set aside automatically)\n" +
                        "- Needs (50%): Rs. " + (int)needs + " (to spend on essentials)\n" +
                        "- Fun (30%): Rs. " + (int)fun + " (for your enjoyment)\n\n" +
                        "Total money to spend this week: Rs. " + (int)spendable + " (80% of your pocket money)\n\n" +
                        "Remember: Try to stick to these categories to build good habits!";

                    JOptionPane.showMessageDialog(this, msg); // Show message
                    setVisible(false); // Hide current frame
                    new DailyFrame(userId, name, 1); // Go to day 1 screen
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); // Show DB error
            }
        }
        if (e.getSource() == backBtn) {
            setVisible(false); // Hide frame
            new WelcomeScreen(); // Go back to welcome screen
        }
    }
}

// Frame for daily expense tracking
class DailyFrame extends JFrame implements ActionListener {
    int userId, dayNum; // Track which user and which day
    String user; // User name
    double weeklyMoney, dailyBudget; // Money info
    FancyLabel infoL, savingsL, spentL; // Labels for info
    FancyField amtF; // Field for entering amount
    JComboBox<String> catC; // ComboBox for category
    FancyButton addBtn, doneBtn, quitBtn; // Action buttons

    DailyFrame(int userId, String user, int dayNum) {
        this.userId = userId;
        this.user = user;
        this.dayNum = dayNum;

        setTitle("Day " + dayNum + " - " + user); // Set window title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app on exit
        setSize(460, 400); // Set window size
        setLocationRelativeTo(null); // Center window

        FancyPanel p = new FancyPanel(); // Use custom panel

        try {
            weeklyMoney = DB.getWeeklyMoney(userId); // Get weekly money from DB
        } catch (Exception e) {
            weeklyMoney = 0;
        }
        dailyBudget =  weeklyMoney / 7; // Calculate daily budget

        infoL = new FancyLabel("Day " + dayNum + " | Daily Budget: Rs " + (int)dailyBudget, 17, true); // Info label
        savingsL = new FancyLabel("Current Savings: Rs " + (int)getSavings(), 16, false); // Savings label
        spentL = new FancyLabel("Spent Today: Rs 0", 16, false); // Spent today label

        amtF = new FancyField(8); // Amount input field
        catC = new JComboBox<>(new String[]{"need", "fun"}); // Category combo box
        catC.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));

        addBtn = new FancyButton("Add Expense"); // Add expense button
        addBtn.addActionListener(this);

        doneBtn = new FancyButton(dayNum < 7 ? "Finish Day" : "Show Weekly Summary"); // Finish day or show summary
        doneBtn.addActionListener(this);

        quitBtn = new FancyButton("Quit"); // Quit button
        quitBtn.addActionListener(this);

        add(p); // Add panel to frame
        addToPanel(p, infoL, 0);
        addToPanel(p, savingsL, 1);
        addToPanel(p, spentL, 2);
        addToPanel(p, new FancyLabel("Amount:", 15, false), 3);
        addToPanel(p, amtF, 4);
        addToPanel(p, new FancyLabel("Category:", 15, false), 5);
        addToPanel(p, catC, 6);
        addToPanel(p, addBtn, 7);
        addToPanel(p, doneBtn, 8);
        addToPanel(p, quitBtn, 9);

        setVisible(true); // Show window
    }

    // Helper to add components to panel
    public void addToPanel(JPanel p, JComponent c, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 5, 40); // Padding
        p.add(c, gbc);
    }

    // Handle button actions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            double amt = 0;
            try { amt = Double.parseDouble(amtF.getText().trim()); } catch (Exception ex) {}
            if (amt <= 0) { // Check for valid amount
                JOptionPane.showMessageDialog(this, "Enter a positive amount!");
                return;
            }
            double spentSoFar = getSpentToday(); // Get today's spending
            if (spentSoFar + amt > dailyBudget) { // Check if exceeds budget
                double remaining = dailyBudget - spentSoFar;
                JOptionPane.showMessageDialog(this,
                    "You can't spend more than your daily budget!\n" +
                    "You have Rs " + (int)remaining + " left to spend today.");
                return;
            }
            String cat = (String)catC.getSelectedItem(); // Get expense category
            try {
                DB.addExpense(userId, dayNum, cat, amt); // Add expense to DB
                spentL.setText("Spent Today: Rs " + (int)getSpentToday()); // Update spent label
                amtF.setText(""); // Clear amount field
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); // Show error
            }
        }
        if (e.getSource() == doneBtn) {
            try {
                double spent = getSpentToday();
                double leftover = dailyBudget - spent;
                if (leftover < 0) leftover = 0; // No negative leftover
                DB.addToSavings(userId, leftover); // Add leftover to savings
                if (dayNum < 7) { // If not last day, go to next day
                    JOptionPane.showMessageDialog(this, "Day finished! Leftover Rs " + (int)leftover + " added to savings.");
                    setVisible(false);
                    new DailyFrame(userId, user, dayNum + 1);
                } else { // If last day, show summary
                    JOptionPane.showMessageDialog(this, "Week finished!");
                    setVisible(false);
                    new SummaryFrame(userId, user);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage()); // Show error
            }
        }
        if (e.getSource() == quitBtn) {
            System.exit(0); // Quit app
        }
    }

    // Get user's savings from DB
    double getSavings() {
        try { return DB.getSavings(userId); }
        catch (Exception e) { return 0; }
    }

    // Get how much user spent today from DB
    double getSpentToday() {
        try { return DB.getTotalSpentToday(userId, dayNum); }
        catch (Exception e) { return 0; }
    }
}

// Frame to show weekly summary at end
class SummaryFrame extends JFrame implements ActionListener {
    FancyButton again, quit; // Buttons to restart or quit

    SummaryFrame(int userId, String user) {
        setTitle("Weekly Summary - " + user); // Set window title
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app on exit
        setSize(350, 350); // Set window size
        setLocationRelativeTo(null); // Center window

        FancyPanel p = new FancyPanel(); // Use custom panel

        double needs = 0, fun = 0, savings = 0, weeklyMoney80 = 0;
        try {
            needs = DB.sumCategory(userId, "need"); // Get needs total
            fun = DB.sumCategory(userId, "fun"); // Get fun total
            savings = DB.getSavings(userId); // Get total savings
            weeklyMoney80 = DB.getWeeklyMoney(userId); // Get weekly money (80%)
        } catch (Exception e) {}

        double initialSavings = weeklyMoney80 * 0.25; // Calculate what was initially saved
        double originalPocketMoney = weeklyMoney80 + initialSavings; // Calculate original pocket money
        double savingsPercent = (savings / originalPocketMoney) * 100; // Calculate percentage saved

        // Choose message based on how well the user saved
        String encouragement;
        if (savingsPercent >= 30) {
            encouragement = "Incredible! You saved way above your target! ";
        } else if (savingsPercent >= 20) {
            encouragement = "Great job! You met your savings goal! ";
        } else if (savingsPercent >= 15) {
            encouragement = "Nice try! You’re close to your savings goal—just a little more next time!";
        } else {
            encouragement = "Let's aim for more savings next time!";
        }

        add(p); // Add panel to frame
        addToPanel(p, new FancyLabel("Weekly Summary for " + user, 18, true), 0); // Add summary label
        addToPanel(p, new FancyLabel("Total Needs Spent: Rs " + (int)needs, 16, false), 1); // Needs spent
        addToPanel(p, new FancyLabel("Total Fun Spent: Rs " + (int)fun, 16, false), 2); // Fun spent
        addToPanel(p, new FancyLabel("Total Saved: Rs " + (int)savings + " (" + ((int)(savingsPercent * 10) / 10.0) + "%)", 16, false), 3); // Savings
        addToPanel(p, new FancyLabel(encouragement, 15, true), 4); // Encouragement message

        again = new FancyButton("Restart"); // Restart button
        again.addActionListener(this);
        quit = new FancyButton("Quit"); // Quit button
        quit.addActionListener(this);
        addToPanel(p, again, 5);
        addToPanel(p, quit, 6);

        setVisible(true); // Show window
    }

    // Helper to add components to panel
    public void addToPanel(JPanel p, JComponent c, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 40, 5, 40); // Padding
        p.add(c, gbc);
    }

    // Handle button actions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == again) {
            setVisible(false); // Hide this window
            new WelcomeScreen(); // Go back to welcome screen
        }
        if (e.getSource() == quit) {
            System.exit(0); // Quit app
        }
    }
}

// Main class to start the program
public class FinanceTracker {
    public static void main(String args[]) {
        new WelcomeScreen(); // Show welcome screen
    }
}
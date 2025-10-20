import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// Custom panel that paints a background image
class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getIconWidth() > 0) {
            background = icon.getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class RestaurantGUI extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;
    JTextArea cartArea;
    double total = 0;
    ArrayList<String> cartItems = new ArrayList<>();
    String selectedCategory = "";
    String customerName = "";
    String tableNumber = "";

    public RestaurantGUI() {
        setTitle("Restaurant Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomePage(), "welcome");
        mainPanel.add(createCategoryPage(), "category");
        mainPanel.add(createMenuPage(), "menu");
        mainPanel.add(createPaymentPage(), "payment");

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");
    }

    // 1. Welcome Page with full background image, title, and input fields
    private JPanel createWelcomePage() {
        // Use the BackgroundPanel with our background image.
        BackgroundPanel panel = new BackgroundPanel("images/welcome_background.jpg");
        panel.setLayout(new BorderLayout());

        // Transparent overlay panel for title at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Welcome to Squad8 Cafe", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 42));
        titleLabel.setForeground(new Color(0, 102, 204)); // A nice shade of blue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Transparent panel for input fields (centered)
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 200));

        JLabel nameLabel = new JLabel("Enter Your Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.black);
        inputPanel.add(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(nameField);

        JLabel tableLabel = new JLabel("Enter Table Number:");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tableLabel.setForeground(Color.black);
        inputPanel.add(tableLabel);
        JTextField tableField = new JTextField();
        tableField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(tableField);

        // Empty label for grid alignment and "Enter Menu" button
        inputPanel.add(new JLabel(""));
        JButton enterBtn = new JButton("Start Order");
        enterBtn.setFont(new Font("Arial", Font.BOLD, 24));
        enterBtn.setBackground(new Color(0, 153, 76));
        enterBtn.setForeground(Color.WHITE);
        inputPanel.add(enterBtn);

        enterBtn.addActionListener(e -> {
            customerName = nameField.getText().trim();
            tableNumber = tableField.getText().trim();
            if (customerName.isEmpty() || tableNumber.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter both your name and table number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                cardLayout.show(mainPanel, "category");
            }
        });

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    // 2. Category Page with category images
    private JPanel createCategoryPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel catTitle = new JLabel("Select a Category", JLabel.CENTER);
        catTitle.setFont(new Font("Serif", Font.BOLD, 36));
        catTitle.setForeground(new Color(0, 102, 204));
        catTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(catTitle, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        gridPanel.setBackground(Color.WHITE);

        // Update the category images (ensure these file names exist)
        String[][] categories = {
                { "Main Course", "images/maincourse.jpg" },
                { "Chinese", "images/chinese.jpg" },
                { "Desserts", "images/desserts.jpg" },
                { "Drinks", "images/drink.jpg" }
        };

        for (String[] cat : categories) {
            // Increase size for clearer visibility
            ImageIcon catIcon = new ImageIcon(cat[1]);
            Image img = catIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            JButton button = new JButton(cat[0], new ImageIcon(img));
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setFont(new Font("Arial", Font.BOLD, 22));
            button.setBackground(new Color(255, 204, 153));
            button.setForeground(Color.BLACK);
            button.addActionListener(e -> {
                selectedCategory = cat[0];
                cardLayout.show(mainPanel, "menu");
            });
            gridPanel.add(button);
        }
        panel.add(gridPanel, BorderLayout.CENTER);

        // Back button to return to Welcome Page
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(204, 0, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "welcome"));
        bottomPanel.add(backBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // 3. Menu Page (Dishes)
    private JPanel createMenuPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title for the Menu Page
        JLabel menuTitle = new JLabel("", JLabel.CENTER);
        menuTitle.setFont(new Font("Serif", Font.BOLD, 36));
        menuTitle.setForeground(new Color(0, 102, 204));
        menuTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(menuTitle, BorderLayout.NORTH);

        JPanel dishesPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        dishesPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dishesPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(dishesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Back and Proceed buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(204, 0, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "category"));
        bottomPanel.add(backBtn);

        JButton toPay = new JButton("Proceed to Payment");
        toPay.setFont(new Font("Arial", Font.BOLD, 18));
        toPay.setBackground(new Color(0, 153, 76));
        toPay.setForeground(Color.WHITE);
        toPay.addActionListener(e -> cardLayout.show(mainPanel, "payment"));
        bottomPanel.add(toPay);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Dishes for each category
        Map<String, Object[][]> allDishes = new HashMap<>();
        allDishes.put("Main Course", new Object[][] {
                { "Pizza", "images/pizza.jpg", 220 },
                { "Burger", "images/burger.jpg", 150 },
                { "Dosa", "images/dosa.jpg", 100 },
                { "Thali", "images/thali.jpeg", 220 }

        });
        allDishes.put("Chinese", new Object[][] {
                { "Noodles", "images/noodles.jpg", 140 },
                { "Spring Roll", "images/spring.jpeg", 190 },
                { "Fried Dumpling", "images/dumpling.jpg", 200 },
                { "Sushi", "images/sushi.jpg", 130 }
        });
        allDishes.put("Desserts", new Object[][] {
                { "Ice Cream", "images/icecream.jpg", 90 },
                { "Cake", "images/cake.jpg", 110 },
                { "Gulab Jamun", "images/jamun.jpg", 80 },
                { "Kaju Katli", "images/katli.jpeg", 150 }
        });
        allDishes.put("Drinks", new Object[][] {
                { "Mocktail", "images/mocktail.jpg", 120 },
                { "Mango Juice", "images/mango.jpg", 120 },
                { "Orange Juice", "images/orange.jpg", 120 },
                { "WaterMelon Juice", "images/melon.jpg", 120 }
        });

        // Update the dishes when the panel is shown
        panel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                menuTitle.setText(selectedCategory + " Menu");
                dishesPanel.removeAll();
                Object[][] dishes = allDishes.get(selectedCategory);
                if (dishes != null) {
                    for (Object[] dish : dishes) {
                        String name = (String) dish[0];
                        String imgPath = (String) dish[1];
                        double price = ((Number) dish[2]).doubleValue();

                        JPanel dishCard = new JPanel(new BorderLayout());
                        dishCard.setBackground(new Color(255, 255, 240));
                        dishCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

                        ImageIcon imgIcon = new ImageIcon(imgPath);
                        if (imgIcon.getIconWidth() > 0) {
                            Image scaledImg = imgIcon.getImage().getScaledInstance(250, 180, Image.SCALE_SMOOTH);
                            dishCard.add(new JLabel(new ImageIcon(scaledImg)), BorderLayout.CENTER);
                        } else {
                            JLabel missing = new JLabel("Image Not Found", JLabel.CENTER);
                            missing.setForeground(Color.RED);
                            dishCard.add(missing, BorderLayout.CENTER);
                        }

                        JLabel nameLabel = new JLabel(name + " - ₹" + price, JLabel.CENTER);
                        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
                        dishCard.add(nameLabel, BorderLayout.NORTH);

                        JButton addBtn = new JButton("Add");
                        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
                        addBtn.setBackground(new Color(0, 153, 76));
                        addBtn.setForeground(Color.WHITE);
                        addBtn.addActionListener(ev -> {
                            cartItems.add(name + " - ₹" + price);
                            total += price;
                            JOptionPane.showMessageDialog(panel, name + " added to cart.", "Item Added",
                                    JOptionPane.INFORMATION_MESSAGE);
                        });
                        dishCard.add(addBtn, BorderLayout.SOUTH);

                        dishesPanel.add(dishCard);
                    }
                }
                dishesPanel.revalidate();
                dishesPanel.repaint();
            }
        });

        return panel;
    }

    // 4. Payment & Receipt Page with decorative image
    private JPanel createPaymentPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Top Panel for Title and decorative image for Payment Page
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel paymentTitle = new JLabel("Payment & Receipt", JLabel.CENTER);
        paymentTitle.setFont(new Font("Serif", Font.BOLD, 36));
        paymentTitle.setForeground(new Color(0, 102, 204));
        paymentTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(paymentTitle, BorderLayout.NORTH);

        // Decorative image for payment page
        ImageIcon decorPayIcon = new ImageIcon("images/decor_payment.jpg");
        JLabel decorPayLabel = new JLabel();
        if (decorPayIcon.getIconWidth() > 0) {
            Image scaledDecor = decorPayIcon.getImage().getScaledInstance(1000, 150, Image.SCALE_SMOOTH);
            decorPayLabel.setIcon(new ImageIcon(scaledDecor));
        } else {
            decorPayLabel.setText("Decorative Image Not Found");
            decorPayLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        topPanel.add(decorPayLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        cartArea = new JTextArea(15, 40);
        cartArea.setEditable(false);
        cartArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(cartArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Color.WHITE);
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(204, 0, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        bottomPanel.add(backBtn);

        JLabel payLabel = new JLabel("Payment Method:");
        payLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(payLabel);
        String[] paymentMethods = { "Cash", "Card", "UPI" };
        JComboBox<String> paymentBox = new JComboBox<>(paymentMethods);
        paymentBox.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(paymentBox);

        JButton printBtn = new JButton("Pay & Print Receipt");
        printBtn.setFont(new Font("Arial", Font.BOLD, 18));
        printBtn.setBackground(new Color(0, 153, 76));
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> {
            cartArea.setText("------------ RECEIPT ------------\n");
            cartArea.append("Customer Name: " + customerName + "\n");
            cartArea.append("Table Number: " + tableNumber + "\n\n");

            for (String item : cartItems) {
                cartArea.append(item + "\n");
            }
            cartArea.append("\nTotal: ₹" + total + "\n");

            if (total > 500) {
                double discount = total * 0.10;
                double finalTotal = total - discount;
                cartArea.append("Discount (10%): -₹" + discount + "\n");
                cartArea.append("Amount to Pay: ₹" + finalTotal + "\n");
            } else {
                cartArea.append("Amount to Pay: ₹" + total + "\n");
            }

            cartArea.append("Payment Mode: " + paymentBox.getSelectedItem() + "\n");
            cartArea.append("Thank you for dining with us!\n");
            saveReceiptToFile();
        });
        bottomPanel.add(printBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Save receipt to a text file
    private void saveReceiptToFile() {
        try {
            String filename = "Receipt_" + customerName.replaceAll("\\s+", "") + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(cartArea.getText());
            writer.close();
            JOptionPane.showMessageDialog(this, "Receipt saved successfully as " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving receipt!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RestaurantGUI().setVisible(true));
    }
}
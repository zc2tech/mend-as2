package de.mendelson.util.examples;

import de.mendelson.util.KeyboardShortcutUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Example dialog demonstrating keyboard shortcuts with tooltips.
 * This shows how to make shortcuts discoverable through hover tooltips.
 *
 * @author S.Heller
 */
public class ExampleDialogWithTooltips extends JDialog {

    private JButton jButtonSave;
    private JButton jButtonCancel;
    private JButton jButtonPrint;
    private JButton jButtonExport;
    private JTextField jTextFieldName;
    private JLabel jLabelHelp;

    public ExampleDialogWithTooltips(JFrame parent) {
        super(parent, "Example Dialog with Tooltips", true);
        initComponents();
        setupKeyboardShortcuts();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add a text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        jTextFieldName = new JTextField(20);
        contentPanel.add(jTextFieldName, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Create buttons with tooltips showing shortcuts
        jButtonSave = new JButton("Save");
        jButtonSave.addActionListener(this::onSave);

        jButtonPrint = new JButton("Print");
        jButtonPrint.addActionListener(this::onPrint);

        jButtonExport = new JButton("Export");
        jButtonExport.addActionListener(this::onExport);

        jButtonCancel = new JButton("Cancel");
        jButtonCancel.addActionListener(this::onCancel);

        buttonPanel.add(jButtonPrint);
        buttonPanel.add(jButtonExport);
        buttonPanel.add(jButtonCancel);
        buttonPanel.add(jButtonSave);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add help label at the bottom
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        jLabelHelp = KeyboardShortcutUtil.createShortcutsHelpLabel();
        helpPanel.add(jLabelHelp);
        add(helpPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Setup keyboard shortcuts with tooltips.
     * This method demonstrates how to add shortcuts that are discoverable.
     */
    private void setupKeyboardShortcuts() {
        // Method 1: Use the comprehensive setup with tooltips
        // This automatically adds tooltips to OK and Cancel buttons
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, jButtonSave, jButtonCancel);

        // Method 2: Add shortcuts to specific buttons with tooltips
        // Cmd/Ctrl+S for Save
        KeyboardShortcutUtil.addButtonShortcutWithTooltip(jButtonSave, KeyEvent.VK_S, "SAVE_ACTION");

        // Cmd/Ctrl+P for Print
        KeyboardShortcutUtil.addButtonShortcutWithTooltip(jButtonPrint, KeyEvent.VK_P, "PRINT_ACTION");

        // Cmd/Ctrl+E for Export
        KeyboardShortcutUtil.addButtonShortcutWithTooltip(jButtonExport, KeyEvent.VK_E, "EXPORT_ACTION");

        // Method 3: Manual tooltip addition to any component
        // You can also add custom tooltips to other components
        jTextFieldName.setToolTipText("Enter your name here");
    }

    /**
     * Alternative: Setup shortcuts without automatic tooltips,
     * but with custom tooltips on specific buttons.
     */
    @SuppressWarnings("unused")
    private void setupKeyboardShortcutsCustomTooltips() {
        // Basic setup without tooltips
        KeyboardShortcutUtil.setupDialogKeyBindings(this, jButtonSave, jButtonCancel);

        // Add shortcuts with custom tooltips
        jButtonSave.setToolTipText("Save the document [" +
            KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_S) + "]");
        KeyboardShortcutUtil.addButtonShortcut(jButtonSave, KeyEvent.VK_S, "SAVE_ACTION");

        jButtonPrint.setToolTipText("Print the document [" +
            KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_P) + "]");
        KeyboardShortcutUtil.addButtonShortcut(jButtonPrint, KeyEvent.VK_P, "PRINT_ACTION");

        jButtonCancel.setToolTipText("Cancel and close [ESC]");
    }

    private void onSave(ActionEvent e) {
        System.out.println("Save clicked - Name: " + jTextFieldName.getText());
        JOptionPane.showMessageDialog(this,
            "Saved! You can use " + KeyboardShortcutUtil.getMenuShortcutKeyName() + "+S to save quickly.",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onPrint(ActionEvent e) {
        System.out.println("Print clicked");
        JOptionPane.showMessageDialog(this,
            "Printing... Try " + KeyboardShortcutUtil.getMenuShortcutKeyName() + "+P next time!",
            "Print", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExport(ActionEvent e) {
        System.out.println("Export clicked");
        JOptionPane.showMessageDialog(this,
            "Exporting... Use " + KeyboardShortcutUtil.getMenuShortcutKeyName() + "+E for quick export!",
            "Export", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onCancel(ActionEvent e) {
        System.out.println("Cancel clicked");
        dispose();
    }

    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set tooltip delay to show faster (optional)
            ToolTipManager.sharedInstance().setInitialDelay(500);
            ToolTipManager.sharedInstance().setDismissDelay(15000);

            JFrame frame = new JFrame("Test Frame");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            ExampleDialogWithTooltips dialog = new ExampleDialogWithTooltips(frame);

            // Show instruction dialog
            JOptionPane.showMessageDialog(frame,
                "<html><b>Try hovering over the buttons!</b><br><br>" +
                "Tooltips will show you the keyboard shortcuts:<br>" +
                "• Save button shows: " + KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_S) + "<br>" +
                "• Print button shows: " + KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_P) + "<br>" +
                "• Export button shows: " + KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_E) + "<br>" +
                "• Cancel button shows: ESC<br><br>" +
                "You can also press ESC or " + KeyboardShortcutUtil.getMenuShortcutKeyName() + "+W to close!</html>",
                "Tooltip Demo",
                JOptionPane.INFORMATION_MESSAGE);

            dialog.setVisible(true);
        });
    }
}

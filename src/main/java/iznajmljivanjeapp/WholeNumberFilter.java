package iznajmljivanjeapp;
import javax.swing.*;
import javax.swing.text.*;

public class WholeNumberFilter extends DocumentFilter {

    private boolean isValid(String text) {
        // Allow empty (so user can clear field)
        if (text.isEmpty()) return true;

        // Must be digits only
        if (!text.matches("\\d+")) return false;

        // Reject leading zeros unless it's just "0"
        if (text.length() > 1 && text.startsWith("0")) return false;

        return true;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) return;
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.insert(offset, string);

        if (isValid(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.replace(offset, offset + length, text == null ? "" : text);

        if (isValid(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        sb.delete(offset, offset + length);

        if (isValid(sb.toString())) {
            super.remove(fb, offset, length);
        }
    }

    // Utility method
    public static void applyTo(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new WholeNumberFilter());
    }

    // Demo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Whole Numbers Only (No Leading Zeros)");
            JTextField field = new JTextField(10);
            WholeNumberFilter.applyTo(field);

            f.add(field);
            f.pack();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
